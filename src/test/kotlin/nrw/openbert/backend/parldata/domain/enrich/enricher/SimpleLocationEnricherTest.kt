package nrw.openbert.backend.parldata.domain.enrich.enricher

import nrw.openbert.backend.parldata.domain.enrich.matcher.RoomMatcher
import nrw.openbert.backend.parldata.domain.enrich.matcher.StreetNameMatcher
import nrw.openbert.backend.parldata.domain.enrich.normalizer.NullableStringNormalizer
import nrw.openbert.backend.parldata.domain.enrich.normalizer.StreetAddressNormalizer
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawLocation
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SimpleLocationEnricherTest {
    private val subject =
        SimpleLocationEnricher(
            NullableStringNormalizer(),
            StreetAddressNormalizer(NullableStringNormalizer()),
            RoomMatcher(),
            StreetNameMatcher(),
        )

    @ParameterizedTest(name = "{0}")
    @MethodSource("enrichmentCases")
    fun `corrects misplaced values without changing raw data`(
        name: String,
        input: LocationValues,
        expected: LocationValues,
    ) {
        val raw = input.toRaw()
        val overrides = subject.enrich(raw, LocationOverrides())
        assertEquals(expected, input.withOverrides(overrides), name)
        assertEquals(input, LocationValues(raw.description, raw.streetAddress, raw.postalCode, raw.locality, raw.room))
        // Corrected values must remain stable when processed again.
        assertEquals(expected, expected.withOverrides(subject.enrich(expected.toRaw(), LocationOverrides())))
    }

    @Test
    fun `unchanged values do not create overrides`() {
        assertEquals(
            LocationOverrides(),
            subject.enrich(LocationValues(postalCode = "01234").toRaw(), LocationOverrides()),
        )
    }

    @Test
    fun `clearing a repeated address produces an explicit null override`() {
        val raw = LocationValues(streetAddress = "42553 Velbert", postalCode = "42553", locality = "Velbert").toRaw()
        val overrides = subject.enrich(raw, LocationOverrides())
        assertTrue(overrides.overrideStreetAddress)
        assertEquals(null, overrides.streetAddress)
        assertFalse(overrides.overridePostalCode)
        assertFalse(overrides.overrideLocality)
    }

    @Test
    fun `uses existing overrides when applying rules`() {
        val raw = LocationValues(postalCode = "42553", locality = "Velbert").toRaw()
        val previous =
            LocationOverrides()
                .withStreetAddressOverride(" 42553 Velbert ")
                .withRoomOverride("Nevigeser Str. 307")
        val overrides = subject.enrich(raw, previous)
        assertEquals("Nevigeser Straße 307", overrides.streetAddress)
        assertTrue(overrides.overrideRoom)
        assertEquals(null, overrides.room)
    }

    data class LocationValues(
        val description: String? = null,
        val streetAddress: String? = null,
        val postalCode: String? = null,
        val locality: String? = null,
        val room: String? = null,
    ) {
        fun toRaw(): RawLocation {
            val system = OparlSystem(OparlUrl("https://example.org/system"))
            return RawLocation(OparlResource(system, OparlUrl("https://example.org/location/1"))).apply {
                description = this@LocationValues.description
                streetAddress = this@LocationValues.streetAddress
                postalCode = this@LocationValues.postalCode
                locality = this@LocationValues.locality
                room = this@LocationValues.room
            }
        }

        fun withOverrides(overrides: LocationOverrides) =
            LocationValues(
                overrides.getDescriptionOverrideOrOriginalValue(description),
                overrides.getStreetAddressOverrideOrOriginalValue(streetAddress),
                overrides.getPostalCodeOverrideOrOriginalValue(postalCode),
                overrides.getLocalityOverrideOrOriginalValue(locality),
                overrides.getRoomOverrideOrOriginalValue(room),
            )
    }

    companion object {
        @JvmStatic
        fun enrichmentCases(): List<Arguments> =
            normalizationCases() + streetAddressCases() + organizationAndRoomCases() + localityCases() +
                roomCases()

        private fun normalizationCases(): List<Arguments> =
            listOf(
                Arguments.of(
                    "trims all supported fields",
                    LocationValues(" Description ", " Hauptstr. 12 ", " 01234 ", " Velbert ", " Raum 41 "),
                    LocationValues("Description", "Hauptstraße 12", "01234", "Velbert", "Raum 41"),
                ),
                Arguments.of(
                    "clears empty fields and dash placeholders",
                    LocationValues(" ", "-", "", "\t", " - "),
                    LocationValues(),
                ),
                Arguments.of(
                    "removes repeated postal code and city and moves the street out of room",
                    LocationValues("Tagespflege Nitz", "42553 Velbert", "42553", "Velbert", "Nevigeser Str. 307"),
                    LocationValues("Tagespflege Nitz", "Nevigeser Straße 307", "42553", "Velbert"),
                ),
                Arguments.of(
                    "clears a repeated address even when no street can be recovered",
                    LocationValues(
                        streetAddress = "42553 Velbert",
                        postalCode = "42553",
                        locality = "Velbert",
                        room = "Aula",
                    ),
                    LocationValues(postalCode = "42553", locality = "Velbert", room = "Aula"),
                ),
                Arguments.of(
                    "preserves a real street address containing postal code and city",
                    LocationValues(
                        streetAddress = "Hauptstraße 12, 42553 Velbert",
                        postalCode = "42553",
                        locality = "Velbert",
                    ),
                    LocationValues(
                        streetAddress = "Hauptstraße 12, 42553 Velbert",
                        postalCode = "42553",
                        locality = "Velbert",
                    ),
                ),
            )

        private fun streetAddressCases(): List<Arguments> =
            listOf(
                Arguments.of(
                    "moves a street into an empty street address",
                    LocationValues(room = "Rathausplatz 1"),
                    LocationValues(streetAddress = "Rathausplatz 1"),
                ),
                Arguments.of(
                    "swaps a room and a street address",
                    LocationValues("VHS", "Raum 41", "42555", "Velbert", "Donnerstraße 13"),
                    LocationValues("VHS", "Donnerstraße 13", "42555", "Velbert", "Raum 41"),
                ),
                Arguments.of(
                    "normalizes an abbreviated street after swapping",
                    LocationValues(streetAddress = "Raum 41", room = "Nevigeser Str. 307"),
                    LocationValues(streetAddress = "Nevigeser Straße 307", room = "Raum 41"),
                ),
                Arguments.of(
                    "does not swap when the street has no house number",
                    LocationValues(streetAddress = "Raum 41", room = "Donnerstraße"),
                    LocationValues(streetAddress = "Raum 41", room = "Donnerstraße"),
                ),
                Arguments.of(
                    "does not swap when only the room matcher matches",
                    LocationValues(streetAddress = "Raum 41", room = "Gebäude 13"),
                    LocationValues(streetAddress = "Raum 41", room = "Gebäude 13"),
                ),
            )

        private fun organizationAndRoomCases(): List<Arguments> =
            listOf(
                Arguments.of(
                    "does not overwrite an existing street address",
                    LocationValues(streetAddress = "Hauptstraße 1", room = "Donnerstraße 13"),
                    LocationValues(streetAddress = "Hauptstraße 1", room = "Donnerstraße 13"),
                ),
                Arguments.of(
                    "clears the split organization name without inferring a city",
                    LocationValues(" AWO Ortsverein Velbert ", postalCode = " AWO ", locality = " Ortsverein Velbert "),
                    LocationValues("AWO Ortsverein Velbert"),
                ),
                Arguments.of(
                    "organization matching takes priority over room terms",
                    LocationValues("Neubau Velbert", postalCode = "Neubau", locality = "Velbert"),
                    LocationValues("Neubau Velbert"),
                ),
                Arguments.of(
                    "organization matching requires an exact description match",
                    LocationValues("AWO Ortsverein Velbert", postalCode = "AWO", locality = "Ortsverein Essen"),
                    LocationValues("AWO Ortsverein Velbert", locality = "AWO Ortsverein Essen"),
                ),
                Arguments.of(
                    "moves a room name from postal code without altering misplaced locality",
                    LocationValues(
                        "Technische Betriebe Velbert",
                        postalCode = "Sitzungssaal",
                        locality = "1, Am Lindenkamp 33",
                    ),
                    LocationValues(
                        "Technische Betriebe Velbert",
                        locality = "1, Am Lindenkamp 33",
                        room = "Sitzungssaal",
                    ),
                ),
            )

        private fun localityCases(): List<Arguments> =
            listOf(
                Arguments.of(
                    "never overwrites an existing room or appends a room term to locality",
                    LocationValues(postalCode = "Sitzungssaal", locality = "Velbert", room = "Raum 41"),
                    LocationValues(postalCode = "Sitzungssaal", locality = "Velbert", room = "Raum 41"),
                ),
                Arguments.of(
                    "treats a blank room as empty",
                    LocationValues(postalCode = "AULA", room = " - "),
                    LocationValues(room = "AULA"),
                ),
                Arguments.of(
                    "rejoins a sentence split across postal code and locality",
                    LocationValues(
                        "wechselnder Sitzungsort",
                        postalCode = "Den",
                        locality = "Sitzungsort entnehmen Sie bitte der Einladung bzw. dem Newseintrag",
                    ),
                    LocationValues(
                        "wechselnder Sitzungsort",
                        locality = "Den Sitzungsort entnehmen Sie bitte der Einladung bzw. dem Newseintrag",
                    ),
                ),
                Arguments.of(
                    "does not reinterpret a valid postal code even if the description repeats it",
                    LocationValues("01234 Velbert", postalCode = "01234", locality = "Velbert"),
                    LocationValues("01234 Velbert", postalCode = "01234", locality = "Velbert"),
                ),
                Arguments.of(
                    "preserves invalid postal codes when there is no locality to rejoin",
                    LocationValues(postalCode = "AWO"),
                    LocationValues(postalCode = "AWO"),
                ),
            )

        private fun roomCases(): List<Arguments> =
            listOf("RAUM 41", "Sitzungssaal", "Aula", "SportHALLE", "Neubau").map { room ->
                Arguments.of(
                    "recognizes room term $room",
                    LocationValues(postalCode = room),
                    LocationValues(room = room),
                )
            }
    }
}
