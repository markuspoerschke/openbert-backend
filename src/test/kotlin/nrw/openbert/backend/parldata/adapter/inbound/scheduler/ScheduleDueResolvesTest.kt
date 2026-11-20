package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import nrw.openbert.backend.parldata.application.resolve.Resolver
import nrw.openbert.backend.parldata.domain.entity.ResolveTarget
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.Instant
import java.util.UUID

class ScheduleDueResolvesTest {
    private val resolveTargetRepository = mock(ResolveTargetRepository::class.java)
    private val resolver = mock(Resolver::class.java)
    private val failureRecorder = mock(ResolveFailureRecorder::class.java)
    private val scheduleDueResolves = ScheduleDueResolves(resolveTargetRepository, resolver, failureRecorder)

    @Test
    fun `resolves due targets directly instead of enqueueing them`() {
        val target = resolveTarget()
        `when`(resolveTargetRepository.findDue(anyInstant(), anyEntityTypes(), anyPageable()))
            .thenReturn(listOf(target))

        scheduleDueResolves.run()

        verify(resolver).run(target.id)
        verifyNoInteractions(failureRecorder)
    }

    @Test
    fun `does nothing when no targets are due`() {
        `when`(resolveTargetRepository.findDue(anyInstant(), anyEntityTypes(), anyPageable()))
            .thenReturn(emptyList())

        scheduleDueResolves.run()

        verify(resolver, never()).run(any(UUID::class.java) ?: UUID.randomUUID())
        verifyNoInteractions(failureRecorder)
    }

    @Test
    fun `records the failure and continues when a resolve fails`() {
        val failing = resolveTarget()
        val succeeding = resolveTarget()
        `when`(resolveTargetRepository.findDue(anyInstant(), anyEntityTypes(), anyPageable()))
            .thenReturn(listOf(failing, succeeding))
        val error = RuntimeException("unresolved reference")
        doThrow(error).`when`(resolver).run(failing.id)

        scheduleDueResolves.run()

        verify(failureRecorder).record(failing.id, error)
        verify(resolver).run(succeeding.id)
        verifyNoMoreInteractions(failureRecorder)
    }

    private fun anyInstant(): Instant = any(Instant::class.java) ?: Instant.EPOCH

    private fun anyEntityTypes(): Collection<OparlType> = any() ?: emptyList()

    private fun anyPageable(): Pageable = any(Pageable::class.java) ?: PageRequest.of(0, 100)

    private fun resolveTarget() = ResolveTarget(rawEntityId = UUID.randomUUID(), entityType = OparlType.PERSON)
}
