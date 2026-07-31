package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import java.util.Date

@MappedSuperclass
abstract class AbstractPaper(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Paper"
    }

    @Column
    var name: String? = null

    @Column
    var reference: String? = null

    @Column
    var date: Date? = null

    @Column
    var paperType: String? = null

    @Column
    var license: String? = null

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()

    @Column
    var web: String? = null

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
