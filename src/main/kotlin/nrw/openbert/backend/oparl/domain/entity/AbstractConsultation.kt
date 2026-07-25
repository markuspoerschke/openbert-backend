package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource

@MappedSuperclass
abstract class AbstractConsultation(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Consultation"
    }

    @Column
    var authoritative: Boolean? = null
        protected set

    @Column
    var role: String? = null
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

    fun applyValuesFrom(other: AbstractConsultation) {
        authoritative = other.authoritative
        role = other.role
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
