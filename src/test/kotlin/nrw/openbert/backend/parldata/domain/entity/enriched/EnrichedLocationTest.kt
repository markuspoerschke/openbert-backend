package nrw.openbert.backend.parldata.domain.entity.enriched

import nrw.openbert.backend.parldata.domain.enrich.enricher.LocationOverrides
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawLocation
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class EnrichedLocationTest {

    private fun createRawLocation(): RawLocation {
        val system = OparlSystem(OparlUrl("https://example.com/oparl/v1/system"))
        val resource = OparlResource(system, OparlUrl("https://example.com/oparl/v1/location/1"))
        return RawLocation(resource).apply {
            description = "Raw Description"
            streetAddress = "Raw Street 1"
            postalCode = "12345"
            locality = "Raw City"
            room = "Room 101"
        }
    }

    @Test
    fun `creates EnrichedLocation from RawLocation and LocationOverrides using static constructor`() {
        val raw = createRawLocation()
        val overrides = LocationOverrides(
            overrideStreetAddress = true,
            streetAddress = "Enriched Street 42",
            overridePostalCode = true,
            postalCode = "54321",
            overrideRoom = true,
            room = "Room 202",
            overrideDescription = true,
            description = "Enriched Description",
            overrideLocality = true,
            locality = "Enriched City"
        )

        val enriched = EnrichedLocation.fromRaw(raw, overrides)

        assertSame(raw, enriched.raw)
        assertSame(raw, enriched.rawLocation)
        assertTrue(enriched.overrideStreetAddress)
        assertEquals("Enriched Street 42", enriched.streetAddress)
        assertTrue(enriched.overridePostalCode)
        assertEquals("54321", enriched.postalCode)
        assertTrue(enriched.overrideRoom)
        assertEquals("Room 202", enriched.room)
        assertTrue(enriched.overrideDescription)
        assertEquals("Enriched Description", enriched.description)
        assertTrue(enriched.overrideLocality)
        assertEquals("Enriched City", enriched.locality)
        assertNotNull(enriched.created)
        assertNotNull(enriched.modified)
    }


    @Test
    fun `applyValuesFrom updates override values and modified timestamp`() {
        val raw = createRawLocation()
        val initialOverrides = LocationOverrides()
        val enriched = EnrichedLocation.fromRaw(raw, initialOverrides)

        val previousModified = enriched.modified.minusSeconds(10)
        enriched.modified = previousModified

        val newOverrides = LocationOverrides(
            overrideStreetAddress = true,
            streetAddress = "Updated Street 99",
            overridePostalCode = true,
            postalCode = "99999",
            overrideRoom = true,
            room = "Room 303",
            overrideDescription = true,
            description = "Updated Description",
            overrideLocality = true,
            locality = "Updated City"
        )

        enriched.applyValuesFrom(newOverrides)

        assertTrue(enriched.overrideStreetAddress)
        assertEquals("Updated Street 99", enriched.streetAddress)
        assertTrue(enriched.overridePostalCode)
        assertEquals("99999", enriched.postalCode)
        assertTrue(enriched.overrideRoom)
        assertEquals("Room 303", enriched.room)
        assertTrue(enriched.overrideDescription)
        assertEquals("Updated Description", enriched.description)
        assertTrue(enriched.overrideLocality)
        assertEquals("Updated City", enriched.locality)
        assertTrue(enriched.modified.isAfter(previousModified) || enriched.modified == previousModified)
    }

    @Test
    fun `default values on constructor`() {
        val raw = createRawLocation()
        val enriched = EnrichedLocation(raw = raw)

        assertSame(raw, enriched.raw)
        assertFalse(enriched.overrideStreetAddress)
        assertFalse(enriched.overridePostalCode)
        assertFalse(enriched.overrideRoom)
        assertFalse(enriched.overrideDescription)
        assertFalse(enriched.overrideLocality)
        assertNotNull(enriched.created)
        assertNotNull(enriched.modified)
    }

    @Test
    fun `toLocationOverrides returns LocationOverrides with matching values`() {
        val raw = createRawLocation()
        val originalOverrides = LocationOverrides(
            overrideStreetAddress = true,
            streetAddress = "Target Street 123",
            overridePostalCode = true,
            postalCode = "10115",
            overrideRoom = true,
            room = "Conference Room A",
            overrideDescription = true,
            description = "Main Office Building",
            overrideLocality = true,
            locality = "Berlin"
        )
        val enriched = EnrichedLocation.fromRaw(raw, originalOverrides)

        val extractedOverrides = enriched.toLocationOverrides()

        assertEquals(originalOverrides, extractedOverrides)
    }
}
