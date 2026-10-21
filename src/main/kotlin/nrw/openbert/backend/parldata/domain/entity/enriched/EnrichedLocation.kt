package nrw.openbert.backend.parldata.domain.entity.enriched

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import nrw.openbert.backend.parldata.domain.enrich.enricher.LocationOverrides
import nrw.openbert.backend.parldata.domain.entity.raw.RawLocation
import java.time.Instant
import java.util.UUID

@Entity(name = "oparl_enriched_location")
class EnrichedLocation(
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id", nullable = false, updatable = false)
    val raw: RawLocation,

    @Id
    @Column(nullable = false, updatable = false)
    val id: UUID? = null,
) {
    val rawLocation: RawLocation
        get() = raw

    @Column(nullable = false)
    var overrideStreetAddress: Boolean = false

    @Column
    var streetAddress: String? = null

    @Column(nullable = false)
    var overridePostalCode: Boolean = false

    @Column
    var postalCode: String? = null

    @Column(nullable = false)
    var overrideRoom: Boolean = false

    @Column
    var room: String? = null

    @Column(nullable = false)
    var overrideDescription: Boolean = false

    @Column(columnDefinition = "TEXT")
    var description: String? = null

    @Column(nullable = false)
    var overrideLocality: Boolean = false

    @Column
    var locality: String? = null

    @Column(nullable = false)
    val created: Instant = Instant.now()

    @Column(nullable = false)
    var modified: Instant = Instant.now()

    fun applyValuesFrom(overrides: LocationOverrides) {
        overrideStreetAddress = overrides.overrideStreetAddress
        streetAddress = overrides.streetAddress
        overridePostalCode = overrides.overridePostalCode
        postalCode = overrides.postalCode
        overrideRoom = overrides.overrideRoom
        room = overrides.room
        overrideDescription = overrides.overrideDescription
        description = overrides.description
        overrideLocality = overrides.overrideLocality
        locality = overrides.locality
        modified = Instant.now()
    }

    fun toLocationOverrides(): LocationOverrides = LocationOverrides(
        overrideStreetAddress = overrideStreetAddress,
        streetAddress = streetAddress,
        overridePostalCode = overridePostalCode,
        postalCode = postalCode,
        overrideRoom = overrideRoom,
        room = room,
        overrideDescription = overrideDescription,
        description = description,
        overrideLocality = overrideLocality,
        locality = locality,
    )

    companion object {
        fun fromRaw(raw: RawLocation, overrides: LocationOverrides = LocationOverrides()): EnrichedLocation {
            return EnrichedLocation(raw = raw).apply {
                applyValuesFrom(overrides)
            }
        }
    }
}
