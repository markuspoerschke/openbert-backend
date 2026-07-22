package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@MappedSuperclass
abstract class AbstractConsultation(systemId: UUID, url: OparlUrl) : AbstractOparlEntity(systemId, url) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Consultation"
    }

    @Column
    private var authoritative: Boolean? = null

    @Column
    private var role: String? = null

    @Column
    private var license: String? = null

    @Column(columnDefinition = "text[]")
    private var keyword: MutableList<String> = mutableListOf()

    @Column
    private var web: String? = null

    fun applyValuesFrom(other: AbstractConsultation) {
        authoritative = other.authoritative
        role = other.role
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
