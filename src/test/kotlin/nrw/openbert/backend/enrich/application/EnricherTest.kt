package nrw.openbert.backend.enrich.application

import nrw.openbert.backend.parldata.application.enrich.Enricher
import nrw.openbert.backend.parldata.application.enrich.EntityEnricher
import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichTargetRepository
import nrw.openbert.backend.parldata.application.resolve.ResolveTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.EnrichTarget
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class EnricherTest {
    private val repository = mock(EnrichTargetRepository::class.java)
    private val resolveTargetEnqueuer = mock(ResolveTargetEnqueuer::class.java)

    @Test
    fun `enriches before scheduling resolution and marks the target as completed`() {
        val target = target(OparlType.LOCATION)
        val entityEnricher = mock(EntityEnricher::class.java)
        `when`(entityEnricher.entityType).thenReturn(OparlType.LOCATION)

        Enricher(repository, listOf(entityEnricher), resolveTargetEnqueuer).run(target.id)

        inOrder(entityEnricher, resolveTargetEnqueuer, repository).apply {
            verify(entityEnricher).enrich(target.rawEntityId)
            verify(resolveTargetEnqueuer).enqueue(target.rawEntityId, OparlType.LOCATION)
            verify(repository).save(target)
        }
        assertCompleted(target)
    }

    @ParameterizedTest
    @EnumSource(OparlType::class)
    fun `passes through every entity type without an enricher`(entityType: OparlType) {
        val target = target(entityType)

        Enricher(repository, emptyList(), resolveTargetEnqueuer).run(target.id)

        inOrder(resolveTargetEnqueuer, repository).apply {
            verify(resolveTargetEnqueuer).enqueue(target.rawEntityId, entityType)
            verify(repository).save(target)
        }
        assertCompleted(target)
    }

    @Test
    fun `a failed enrichment does not schedule resolution or complete the target`() {
        val target = target(OparlType.LOCATION)
        val entityEnricher = mock(EntityEnricher::class.java)
        `when`(entityEnricher.entityType).thenReturn(OparlType.LOCATION)
        doThrow(IllegalStateException("Enrichment failed")).`when`(entityEnricher).enrich(target.rawEntityId)

        assertFailsWith<IllegalStateException> {
            Enricher(repository, listOf(entityEnricher), resolveTargetEnqueuer).run(target.id)
        }

        verifyNoInteractions(resolveTargetEnqueuer)
        assertNull(target.lastEnrichedAt)
        assertEquals(1, target.referenceCount)
        assertNotNull(target.jobId)
        assertNotNull(target.scheduledAt)
    }

    @Test
    fun `a missing target does not schedule resolution`() {
        val targetId = UUID.randomUUID()
        `when`(repository.findById(targetId)).thenReturn(Optional.empty())

        assertFailsWith<IllegalArgumentException> {
            Enricher(repository, emptyList(), resolveTargetEnqueuer).run(targetId)
        }

        verifyNoInteractions(resolveTargetEnqueuer)
    }

    private fun target(entityType: OparlType): EnrichTarget {
        val target =
            EnrichTarget(rawEntityId = UUID.randomUUID(), entityType = entityType)
                .enqueue()
                .markAsScheduled(UUID.randomUUID())
        `when`(repository.findById(target.id)).thenReturn(Optional.of(target))
        return target
    }

    private fun assertCompleted(target: EnrichTarget) {
        assertNotNull(target.lastEnrichedAt)
        assertEquals(0, target.referenceCount)
        assertNull(target.jobId)
        assertNull(target.scheduledAt)
    }
}
