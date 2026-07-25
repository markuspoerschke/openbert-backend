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
        protected set

    @Column
    var reference: String? = null
        protected set

    @Column
    var date: Date? = null
        protected set

    @Column
    var paperType: String? = null
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
