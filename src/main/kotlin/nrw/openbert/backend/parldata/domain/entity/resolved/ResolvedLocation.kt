package nrw.openbert.backend.parldata.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.enrich.enricher.LocationOverrides
import nrw.openbert.backend.parldata.domain.entity.AbstractLocation
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawLocation

@Entity(name = "oparl_resolved_location")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedLocation(
    resource: OparlResource,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "body_id", nullable = false, updatable = false)
    val body: ResolvedBody,
) : AbstractLocation(resource) {
    fun applyOverrides(overrides: LocationOverrides) {
        streetAddress = overrides.getStreetAddressOverrideOrOriginalValue(streetAddress)
        postalCode = overrides.getPostalCodeOverrideOrOriginalValue(postalCode)
        room = overrides.getRoomOverrideOrOriginalValue(room)
        description = overrides.getDescriptionOverrideOrOriginalValue(description)
        locality = overrides.getLocalityOverrideOrOriginalValue(locality)
    }

    companion object {
        fun fromRaw(
            raw: RawLocation,
            body: ResolvedBody,
        ): ResolvedLocation = ResolvedLocation(raw.resource, body).apply { applyValuesFrom(raw) }
    }
}
