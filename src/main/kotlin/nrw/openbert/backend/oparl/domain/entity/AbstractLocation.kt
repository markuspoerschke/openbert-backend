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
    private var description: String? = null

    @Column
    private var streetAddress: String? = null

    @Column
    private var postalCode: String? = null

    @Column
    private var locality: String? = null

    @Column
    private var subLocality: String? = null

    @Column
    private var room: String? = null

    @Column(columnDefinition = "text[]")
    private var keyword: MutableList<String> = mutableListOf()

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
