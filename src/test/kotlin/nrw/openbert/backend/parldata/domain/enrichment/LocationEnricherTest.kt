package nrw.openbert.backend.parldata.domain.enrichment

import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedBody
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedLocation
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LocationEnricherTest {
    @Test
    fun `normalizes whitespace before matching address fields`() {
        val location = location().apply {
            description = "  Tagespflege\t Nitz  "
            streetAddress = " 42553\u00a0 Velbert "
            postalCode = " 42553 "
            locality = " Velbert\n"
            subLocality = "  Stadtteil   Mitte "
            room = " Nevigeser  Str.\t307 "
        }

        LocationEnricher.enrich(location)

        assertEquals("Tagespflege Nitz", location.description)
        assertEquals("Nevigeser Straße 307", location.streetAddress)
        assertEquals("42553", location.postalCode)
        assertEquals("Velbert", location.locality)
        assertEquals("Stadtteil Mitte", location.subLocality)
        assertNull(location.room)
    }

    @Test
    fun `treats blank address fields as empty`() {
        val location = location().apply {
            description = " \t"
            streetAddress = "\n"
            postalCode = " "
            locality = "\u00a0"
            subLocality = " "
            room = "\t"
        }

        LocationEnricher.enrich(location)

        assertNull(location.description)
        assertNull(location.streetAddress)
        assertNull(location.postalCode)
        assertNull(location.locality)
        assertNull(location.subLocality)
        assertNull(location.room)
    }

    @Test
    fun `clears duplicate postal code and city without removing a room`() {
        val location = location().apply {
            streetAddress = "42553 Velbert"
            postalCode = "42553"
            locality = "Velbert"
            room = "Ratssaal"
        }

        LocationEnricher.enrich(location)

        assertNull(location.streetAddress)
        assertEquals("Ratssaal", location.room)
    }

    @ParameterizedTest
    @CsvSource(
        "Raum 41, Donnerstraße 13, Donnerstraße 13, Raum 41",
        "Ratssaal, Hauptstr. 4a, Hauptstraße 4a, Ratssaal",
        "Raum 41, Rathaus, Raum 41, Rathaus",
        "VHS, Donnerstraße 13, VHS, Donnerstraße 13",
        "Raum 41, Saal 13, Raum 41, Saal 13",
        "Rathausplatz 1, Donnerstraße 13, Rathausplatz 1, Donnerstraße 13",
        "Saalstraße 1, Donnerstraße 13, Saalstraße 1, Donnerstraße 13",
        "Hallenweg 1, Donnerstraße 13, Hallenweg 1, Donnerstraße 13",
        "42553 Velbert West, Ratssaal, 42553 Velbert West, Ratssaal",
    )
    fun `swaps only when both room and street address match`(
        street: String, roomValue: String, expectedStreet: String, expectedRoom: String,
    ) {
        val location = location().apply {
            streetAddress = street
            room = roomValue
            postalCode = "42553"
            locality = "Velbert"
        }

        LocationEnricher.enrich(location)

        assertEquals(expectedStreet, location.streetAddress)
        assertEquals(expectedRoom, location.room)
    }

    @ParameterizedTest
    @ValueSource(strings = ["SITZUNGSSAAL", "Raum 41", "Aula", "Sporthalle", "Neubau"])
    fun `moves complete room terms out of invalid postal codes`(roomValue: String) {
        val location = location().apply {
            postalCode = roomValue
            locality = "1, Am Lindenkamp 33"
            room = " "
        }

        LocationEnricher.enrich(location)

        assertNull(location.postalCode)
        assertEquals(roomValue, location.room)
        assertEquals("1, Am Lindenkamp 33", location.locality)
    }

    @Test
    fun `does not overwrite a room or merge another room into locality`() {
        val location = location().apply {
            postalCode = "Sitzungssaal"
            locality = "Velbert"
            room = "Raum 41"
        }

        LocationEnricher.enrich(location)

        assertEquals("Sitzungssaal", location.postalCode)
        assertEquals("Velbert", location.locality)
        assertEquals("Raum 41", location.room)
    }

    @Test
    fun `organization match takes precedence even when its name contains a room term`() {
        val location = location().apply {
            description = "Saalbau Verein Velbert"
            postalCode = "Saalbau"
            locality = "Verein Velbert"
        }

        LocationEnricher.enrich(location)

        assertNull(location.postalCode)
        assertNull(location.locality)
        assertNull(location.room)
    }

    @Test
    fun `only clears an organization name on an exact combined match`() {
        val location = location().apply {
            description = "AWO Ortsverein Velbert"
            postalCode = "AWO"
            locality = "Ortsverein Essen"
        }

        LocationEnricher.enrich(location)

        assertNull(location.postalCode)
        assertEquals("AWO Ortsverein Essen", location.locality)
    }

    @Test
    fun `does not clear a valid postal code even when the address repeats the description`() {
        val location = location().apply {
            description = "01234 Velbert"
            postalCode = "01234"
            locality = "Velbert"
        }

        LocationEnricher.enrich(location)

        assertEquals("01234", location.postalCode)
        assertEquals("Velbert", location.locality)
    }

    @Test
    fun `leaves an unknown postal value without locality untouched`() {
        val location = location().apply { postalCode = "Unbekannt" }

        LocationEnricher.enrich(location)

        assertEquals("Unbekannt", location.postalCode)
        assertNull(location.locality)
    }

    @ParameterizedTest
    @CsvSource(
        "Nevigeser Str. 307, Nevigeser Straße 307",
        "Hauptstr. 4 a, Hauptstraße 4 a",
        "Hauptstr. 4-6, Hauptstraße 4-6",
        "Am Lindenkamp 33, Am Lindenkamp 33",
        "Rathausplatz 1/2, Rathausplatz 1/2",
        "Rathaus, NULL",
        "Raum 41, NULL",
        "Donnerstraße, NULL",
        nullValues = ["NULL"],
    )
    fun `moves only recognizable street addresses and expands abbreviations`(roomValue: String, expected: String?) {
        val location = location().apply { room = roomValue }

        LocationEnricher.enrich(location)
        LocationEnricher.enrich(location)

        assertEquals(expected, location.streetAddress)
        assertEquals(if (expected == null) roomValue else null, location.room)
    }

    private fun location(): ResolvedLocation {
        val system = OparlSystem(OparlUrl("https://oparl.example.org"))
        val resource = OparlResource(system, OparlUrl("https://oparl.example.org/location/1"))
        val body = ResolvedBody(OparlResource(system, OparlUrl("https://oparl.example.org/body/1")))
        return ResolvedLocation(resource, body)
    }
}
