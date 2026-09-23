package nrw.openbert.backend.parldata.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@MappedSuperclass
abstract class AbstractLocation(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Location"
    }

    @Column(columnDefinition = "TEXT")
    var description: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    var geojson: Map<String, Any?>? = null

    @Column
    var streetAddress: String? = null

    @Column
    var postalCode: String? = null

    @Column
    var locality: String? = null

    @Column
    var subLocality: String? = null

    @Column
    var room: String? = null

    @Column
    var license: String? = null

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()

    @Column
    var web: String? = null

    fun applyValuesFrom(other: AbstractLocation) {
        description = other.description
        geojson = other.geojson
        streetAddress = other.streetAddress
        postalCode = other.postalCode
        locality = other.locality
        subLocality = other.subLocality
        room = other.room
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
