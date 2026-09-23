package nrw.openbert.backend.oparl.domain.repository.raw

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawAgendaItem
import nrw.openbert.backend.parldata.domain.entity.raw.RawMeeting
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawAgendaItemRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawMeetingRepository
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.Date
import java.util.UUID
import kotlin.test.assertEquals

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class RawReservedIdentifierPersistenceTest(
    @Autowired private val entityManager: EntityManager,
    @Autowired private val agendaItemRepository: RawAgendaItemRepository,
    @Autowired private val meetingRepository: RawMeetingRepository,
) {
    @Test
    fun `persists raw entities with quoted reserved column names`() {
        val system = entityManager.find(
            OparlSystem::class.java,
            UUID.fromString("9352fa21-68c6-4f4e-8832-08a0454ff179"),
        )
        val end = Date.from(Instant.parse("2026-07-31T12:00:00Z"))
        val meeting = RawMeeting(resource(system, "meeting/quoted-columns")).apply {
            this.end = end
        }
        val agendaItem = RawAgendaItem(resource(system, "agenda-item/quoted-columns")).apply {
            order = 3
            this.end = end
        }

        meetingRepository.saveAndFlush(meeting)
        agendaItemRepository.saveAndFlush(agendaItem)
        entityManager.clear()

        assertEquals(end, meetingRepository.findById(meeting.id).orElseThrow().end)
        assertEquals(3, agendaItemRepository.findById(agendaItem.id).orElseThrow().order)
        assertEquals(end, agendaItemRepository.findById(agendaItem.id).orElseThrow().end)
    }

    private fun resource(system: OparlSystem, path: String) =
        OparlResource(system, OparlUrl("https://oparl-demo.example.org/oparl/v1.1/$path"))
}
