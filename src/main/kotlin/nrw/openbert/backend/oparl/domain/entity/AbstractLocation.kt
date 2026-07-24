package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@MappedSuperclass
abstract class AbstractLocation(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Location"
    }

    @Column
    var description: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    var geojson: Map<String, Any?>? = null
        protected set

    @Column
    var streetAddress: String? = null
        protected set

    @Column
    var postalCode: String? = null
        protected set

    @Column
    var locality: String? = null
        protected set

    @Column
    var subLocality: String? = null
        protected set

    @Column
    var room: String? = null
        protected set

    @Column
    var license: String? = null
        protected set

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()
        protected set

    @Column
    var web: String? = null
        protected set

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
