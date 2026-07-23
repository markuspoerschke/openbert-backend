package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.Date
import java.util.UUID

@MappedSuperclass
abstract class AbstractPaper(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Paper"
    }

    @Column
    private var name: String? = null

    @Column
    private var reference: String? = null

    @Column
    private var date: Date? = null

    @Column
    private var paperType: String? = null

    @Column
    private var license: String? = null

    @Column(columnDefinition = "text[]")
    private var keyword: MutableList<String> = mutableListOf()

    @Column
    private var web: String? = null

    fun applyValuesFrom(other: AbstractPaper) {
        name = other.name
        reference = other.reference
        date = other.date
        paperType = other.paperType
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
