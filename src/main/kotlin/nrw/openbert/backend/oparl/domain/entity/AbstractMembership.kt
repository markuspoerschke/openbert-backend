package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import java.util.*

@MappedSuperclass
abstract class AbstractMembership(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Membership"
    }

    @Column
    var role: String? = null
        protected set

    @Column
    var votingRight: Boolean? = null
        protected set

    @Column
    var startDate: Date? = null
        protected set

    @Column
    var endDate: Date? = null
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

    fun applyValuesFrom(other: AbstractMembership) {
        role = other.role
        votingRight = other.votingRight
        startDate = other.startDate
        endDate = other.endDate
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
