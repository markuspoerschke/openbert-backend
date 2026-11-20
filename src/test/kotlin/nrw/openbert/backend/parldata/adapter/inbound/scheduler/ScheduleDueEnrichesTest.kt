package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import nrw.openbert.backend.parldata.application.enrich.Enricher
import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichTargetRepository
import nrw.openbert.backend.parldata.domain.entity.EnrichTarget
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.Instant
import java.util.UUID

class ScheduleDueEnrichesTest {
    private val enrichTargetRepository = mock(EnrichTargetRepository::class.java)
    private val enricher = mock(Enricher::class.java)
    private val failureRecorder = mock(EnrichFailureRecorder::class.java)
    private val scheduleDueEnriches = ScheduleDueEnriches(enrichTargetRepository, enricher, failureRecorder)

    @Test
    fun `enriches due targets directly instead of enqueueing them`() {
        val target = enrichTarget()
        `when`(enrichTargetRepository.findDue(anyInstant(), anyPageable())).thenReturn(listOf(target))

        scheduleDueEnriches.run()

        verify(enricher).run(target.id)
        verifyNoMoreInteractions(enricher, failureRecorder)
    }

    @Test
    fun `does nothing when no targets are due`() {
        `when`(enrichTargetRepository.findDue(anyInstant(), anyPageable())).thenReturn(emptyList())

        scheduleDueEnriches.run()

        verifyNoMoreInteractions(enricher, failureRecorder)
    }

    @Test
    fun `records the failure and continues when an enrichment fails`() {
        val failing = enrichTarget()
        val succeeding = enrichTarget()
        `when`(enrichTargetRepository.findDue(anyInstant(), anyPageable())).thenReturn(listOf(failing, succeeding))
        val error = RuntimeException("enrichment backend unavailable")
        doThrow(error).`when`(enricher).run(failing.id)

        scheduleDueEnriches.run()

        verify(failureRecorder).record(failing.id, error)
        verify(enricher).run(succeeding.id)
        verifyNoMoreInteractions(failureRecorder)
    }

    private fun anyInstant(): Instant = any(Instant::class.java) ?: Instant.EPOCH

    private fun anyPageable(): Pageable = any(Pageable::class.java) ?: PageRequest.of(0, 100)

    private fun enrichTarget() = EnrichTarget(rawEntityId = UUID.randomUUID(), entityType = OparlType.LOCATION)
}
