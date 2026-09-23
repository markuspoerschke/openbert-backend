package nrw.openbert.backend.parldata.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource

@MappedSuperclass
abstract class AbstractConsultation(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Consultation"
    }

    @Column
    var authoritative: Boolean? = null

    @Column
    var role: String? = null

    @Column
    var license: String? = null

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()

    @Column
    var web: String? = null

    fun applyValuesFrom(other: AbstractConsultation) {
        authoritative = other.authoritative
        role = other.role
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
