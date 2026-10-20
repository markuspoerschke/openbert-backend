package nrw.openbert.backend.resolve.application

import nrw.openbert.backend.parldata.application.resolve.LocationEnricher
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawLocation
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LocationEnricherTest {
    private val enricher = LocationEnricher()
    private val system = OparlSystem(OparlUrl("https://oparl.example.org/system"))

    private fun location(
        description: String? = null,
        streetAddress: String? = null,
        postalCode: String? = null,
        locality: String? = null,
        room: String? = null,
    ): RawLocation = RawLocation(OparlResource(system, OparlUrl("https://oparl.example.org/location/${java.util.UUID.randomUUID()}"))).apply {
        this.description = description
        this.streetAddress = streetAddress
        this.postalCode = postalCode
        this.locality = locality
        this.room = room
    }

    @Test
    fun `clears street address that repeats postal code and locality`() {
        val location = location(
            description = "Tagespflege Nitz",
            streetAddress = "42553 Velbert",
            postalCode = "42553",
            locality = "Velbert",
            room = "Nevigeser Str. 307",
        )

        enricher.enrich(location)

        assertNull(location.streetAddress)
        assertEquals("Nevigeser Straße 307", location.room)
    }

    @Test
    fun `keeps street address that differs from postal code and locality`() {
        val location = location(
            streetAddress = "Nevigeser Straße 307",
            postalCode = "42553",
            locality = "Velbert",
        )

        enricher.enrich(location)

        assertEquals("Nevigeser Straße 307", location.streetAddress)
    }

    @Test
    fun `expands street abbreviations`() {
        val location = location(streetAddress = "Nevigeser Str. 307")

        enricher.enrich(location)

        assertEquals("Nevigeser Straße 307", location.streetAddress)
    }

    @Test
    fun `clears organization name stored in postal code and locality`() {
        val location = location(
            description = "AWO Ortsverein Velbert",
            postalCode = "AWO",
            locality = "Ortsverein Velbert",
        )

        enricher.enrich(location)

        assertNull(location.postalCode)
        assertNull(location.locality)
    }

    @Test
    fun `moves room name from postal code to empty room`() {
        val location = location(
            description = "Technische Betriebe Velbert",
            postalCode = "Sitzungssaal",
            locality = "1, Am Lindenkamp 33",
        )

        enricher.enrich(location)

        assertEquals("Sitzungssaal", location.room)
        assertNull(location.postalCode)
    }

    @Test
    fun `never overwrites an existing room value`() {
        val location = location(postalCode = "Sitzungssaal", room = "Raum 41")

        enricher.enrich(location)

        assertEquals("Raum 41", location.room)
        assertEquals("Sitzungssaal", location.postalCode)
    }

    @Test
    fun `swaps street address and room when both are clearly misplaced`() {
        val location = location(
            description = "VHS",
            streetAddress = "Raum 41",
            postalCode = "42555",
            locality = "Velbert",
            room = "Donnerstraße 13",
        )

        enricher.enrich(location)

        assertEquals("Donnerstraße 13", location.streetAddress)
        assertEquals("Raum 41", location.room)
    }

    @Test
    fun `keeps street address and room when only one is misplaced`() {
        val location = location(
            streetAddress = "Raum 41",
            room = "Nebeneingang",
        )

        enricher.enrich(location)

        assertEquals("Raum 41", location.streetAddress)
        assertEquals("Nebeneingang", location.room)
    }

    @Test
    fun `rejoins first word of locality stored in postal code`() {
        val location = location(
            description = "wechselnder Sitzungsort",
            postalCode = "Den",
            locality = "Sitzungsort entnehmen Sie bitte der Einladung bzw. dem Newseintrag",
        )

        enricher.enrich(location)

        assertNull(location.postalCode)
        assertEquals("Den Sitzungsort entnehmen Sie bitte der Einladung bzw. dem Newseintrag", location.locality)
    }

    @Test
    fun `trims and collapses whitespace in all fields`() {
        val location = location(
            description = "  Tagespflege   Nitz ",
            streetAddress = " Nevigeser Str.  307 ",
        )

        enricher.enrich(location)

        assertEquals("Tagespflege Nitz", location.description)
        assertEquals("Nevigeser Straße 307", location.streetAddress)
    }

    @Test
    fun `keeps valid postal code`() {
        val location = location(postalCode = "42553", locality = "Velbert")

        enricher.enrich(location)

        assertEquals("42553", location.postalCode)
        assertEquals("Velbert", location.locality)
    }
}
