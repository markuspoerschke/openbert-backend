package nrw.openbert.backend.parldata.adapter.inbound.api.specification

import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Predicate
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.domain.entity.AbstractOparlEntity
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedBody
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedOrganization
import org.springframework.data.jpa.domain.Specification
import java.time.Instant
import java.util.UUID

/**
 * Maps an OParl 1.1 external list request to a JPA specification: the body/organization scoping
 * plus the filter parameters `created_since`, `created_until`, `modified_since`, `modified_until`.
 *
 * Per OParl 1.1, deleted objects are only included when `modified_since` is set (update mechanism)
 * and must be omitted otherwise.
 */
fun <T : AbstractOparlEntity> OparlPaginationRequestDto.toListSpecification(
    bodyId: UUID? = null,
    organizationId: UUID? = null,
): Specification<T> =
    Specification { root, query, cb ->
        val predicates = mutableListOf<Predicate>()
        if (bodyId != null) {
            predicates += cb.equal(root.get<ResolvedBody>("body").get<UUID>("id"), bodyId)
        }
        if (organizationId != null) {
            query?.distinct(true)
            val organizations: Join<T, ResolvedOrganization> = root.join("organizations")
            predicates += cb.equal(organizations.get<UUID>("id"), organizationId)
        }
        createdSince?.let { predicates += cb.greaterThanOrEqualTo(root.get<Instant>("created"), it.toInstant()) }
        createdUntil?.let { predicates += cb.lessThanOrEqualTo(root.get<Instant>("created"), it.toInstant()) }
        modifiedSince?.let { predicates += cb.greaterThanOrEqualTo(root.get<Instant>("modified"), it.toInstant()) }
        modifiedUntil?.let { predicates += cb.lessThanOrEqualTo(root.get<Instant>("modified"), it.toInstant()) }

        if (modifiedSince == null) {
            predicates += cb.isFalse(root.get<Boolean>("deleted"))
        }

        cb.and(*predicates.toTypedArray())
    }
