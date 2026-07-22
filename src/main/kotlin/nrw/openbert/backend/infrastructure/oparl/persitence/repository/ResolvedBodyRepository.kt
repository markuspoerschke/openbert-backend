package nrw.openbert.backend.infrastructure.oparl.persitence.repository

import nrw.openbert.backend.domain.oparl.entity.resolved.ResolvedBody
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ResolvedBodyRepository : JpaRepository<ResolvedBody, UUID>