package nrw.openbert.backend.parldata.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.enums.OparlType
import java.util.Date

@MappedSuperclass
abstract class AbstractPaper(
    resource: OparlResource,
) : AbstractOparlEntity(resource) {
    @Column(length = 500)
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

    override fun getType() = OparlType.PAPER
}
