package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.application.port.model.BodyResourceCounts
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedBody
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ResolvedBodyRepository :
    JpaRepository<ResolvedBody, UUID>,
    JpaSpecificationExecutor<ResolvedBody> {
    fun findByResource(resource: OparlResource): ResolvedBody?

    @Query(
        """
        SELECT
            (SELECT COUNT(p) FROM oparl_resolved_person p WHERE p.body = b AND p.deleted = FALSE) AS persons,
            (SELECT COUNT(o) FROM oparl_resolved_organization o WHERE o.body = b AND o.deleted = FALSE) AS organizations,
            (SELECT COUNT(p) FROM oparl_resolved_paper p WHERE p.body = b AND p.deleted = FALSE) AS papers,
            (SELECT COUNT(f) FROM oparl_resolved_file f WHERE f.body = b AND f.deleted = FALSE) AS files,
            (SELECT COUNT(m) FROM oparl_resolved_meeting m WHERE m.body = b AND m.deleted = FALSE) AS meetings,
            (SELECT COUNT(a) FROM oparl_resolved_agendaItem a WHERE a.body = b AND a.deleted = FALSE) AS agendaItems,
            (SELECT COUNT(l) FROM oparl_resolved_location l WHERE l.body = b AND l.deleted = FALSE) AS locations,
            (SELECT COUNT(c) FROM oparl_resolved_consultation c WHERE c.body = b AND c.deleted = FALSE) AS consultations,
            (SELECT COUNT(l) FROM oparl_resolved_legislativeTerm l
                WHERE l.body = b AND l.deleted = FALSE) AS legislativeTerms,
            (SELECT COUNT(m) FROM oparl_resolved_membership m WHERE m.body = b AND m.deleted = FALSE) AS memberships
        FROM oparl_resolved_body b
        WHERE b.id = :bodyId
        """,
    )
    fun findResourceCounts(bodyId: UUID): BodyResourceCounts?
}
