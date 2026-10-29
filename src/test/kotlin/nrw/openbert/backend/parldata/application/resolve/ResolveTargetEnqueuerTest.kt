package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.ResolveTarget
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawPaper
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResolveTargetEnqueuerTest {
    private val repository = mock(ResolveTargetRepository::class.java)
    private val enqueuer = ResolveTargetEnqueuer(repository)
    private val system = OparlSystem(OparlUrl("https://oparl.example.org/system"))

    @Test
    fun `creates a resolve target for a crawled raw entity`() {
        val raw = RawPaper(OparlResource(system, OparlUrl("https://oparl.example.org/paper/1")))
        `when`(repository.findByRawEntityIdAndEntityType(raw.id, OparlType.PAPER)).thenReturn(null)

        enqueuer.enqueue(raw)

        val target = captureSavedTarget()
        assertEquals(raw.id, target.rawEntityId)
        assertEquals(OparlType.PAPER, target.entityType)
        assertEquals(1, target.referenceCount)
        assertNull(target.lastResolvedAt)
    }

    @Test
    fun `requeues an existing resolve target when raw data is crawled again`() {
        val raw = RawPaper(OparlResource(system, OparlUrl("https://oparl.example.org/paper/1")))
        val target = ResolveTarget.create(raw).apply { markAsResolved() }
        `when`(repository.findByRawEntityIdAndEntityType(raw.id, OparlType.PAPER)).thenReturn(target)

        enqueuer.enqueue(raw)

        assertEquals(1, target.referenceCount)
        verify(repository).save(target)
    }

    private fun captureSavedTarget(): ResolveTarget {
        val captor = ArgumentCaptor.forClass(ResolveTarget::class.java)
        verify(repository).save(captor.capture())
        return captor.value
    }
}
