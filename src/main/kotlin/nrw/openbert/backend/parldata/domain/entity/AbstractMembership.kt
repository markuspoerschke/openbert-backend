package nrw.openbert.backend.parldata.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.enums.OparlType
import java.util.Date

@MappedSuperclass
abstract class AbstractMembership(
    resource: OparlResource,
) : AbstractOparlEntity(resource) {
    @Column
    var role: String? = null

    @Column
    var votingRight: Boolean? = null

    @Column
    var startDate: Date? = null

    @Column
    var endDate: Date? = null

    @Column
    var license: String? = null

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()

    @Column
    var web: String? = null

    fun applyValuesFrom(other: AbstractMembership) {
        role = other.role
        votingRight = other.votingRight
        startDate = other.startDate
        endDate = other.endDate
        license = other.license
        keyword = other.keyword
        web = other.web
    }

    override fun getType() = OparlType.MEMBERSHIP
}
