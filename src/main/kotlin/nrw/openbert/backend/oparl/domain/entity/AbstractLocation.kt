package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@MappedSuperclass
abstract class AbstractLocation(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Location"
    }

    @Column
    var description: String? = null
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

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()
        protected set

    fun applyValuesFrom(other: AbstractLocation) {
        description = other.description
        streetAddress = other.streetAddress
        postalCode = other.postalCode
        locality = other.locality
        subLocality = other.subLocality
        room = other.room
        keyword = other.keyword
    }
}
