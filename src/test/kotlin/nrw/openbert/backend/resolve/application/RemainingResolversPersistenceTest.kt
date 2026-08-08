package nrw.openbert.backend.resolve.application

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.oparl.domain.entity.AbstractOparlEntity
import nrw.openbert.backend.oparl.domain.entity.raw.RawAgendaItem
import nrw.openbert.backend.oparl.domain.entity.raw.RawConsultation
import nrw.openbert.backend.oparl.domain.entity.raw.RawFile
import nrw.openbert.backend.oparl.domain.entity.raw.RawLegislativeTerm
import nrw.openbert.backend.oparl.domain.entity.raw.RawLocation
import nrw.openbert.backend.oparl.domain.entity.raw.RawMeeting
import nrw.openbert.backend.oparl.domain.entity.raw.RawMembership
import nrw.openbert.backend.oparl.domain.entity.raw.RawPaper
import nrw.openbert.backend.oparl.domain.entity.raw.RawPerson
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedAgendaItemRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedConsultationRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedFileRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedLegislativeTermRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedLocationRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedMeetingRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedMembershipRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedPaperRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedPersonRepository
import nrw.openbert.backend.resolve.domain.entity.ResolveTarget
import nrw.openbert.backend.resolve.domain.repository.ResolveTargetRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class RemainingResolversPersistenceTest(
    @Autowired private val resolver: Resolver,
    @Autowired private val targetRepository: ResolveTargetRepository,
    @Autowired private val entityManager: EntityManager,
    @Autowired private val agendaItemRepository: ResolvedAgendaItemRepository,
    @Autowired private val consultationRepository: ResolvedConsultationRepository,
    @Autowired private val fileRepository: ResolvedFileRepository,
    @Autowired private val legislativeTermRepository: ResolvedLegislativeTermRepository,
    @Autowired private val locationRepository: ResolvedLocationRepository,
    @Autowired private val meetingRepository: ResolvedMeetingRepository,
    @Autowired private val membershipRepository: ResolvedMembershipRepository,
    @Autowired private val paperRepository: ResolvedPaperRepository,
    @Autowired private val personRepository: ResolvedPersonRepository,
) {
    @Test
    fun `resolves every remaining entity type with its relationships`() {
        val legislativeTerm = resolve(RawLegislativeTerm::class.java, "10000000-0000-4000-8000-000000000001")
        val paper = resolve(RawPaper::class.java, "10000000-0000-4000-8000-000000000003")
        val person = resolve(RawPerson::class.java, "10000000-0000-4000-8000-000000000004")
        val location = resolve(RawLocation::class.java, "10000000-0000-4000-8000-000000000005")
        val membership = resolve(RawMembership::class.java, "10000000-0000-4000-8000-000000000006")
        val file = resolve(RawFile::class.java, "aaaaaaaa-aaaa-4aaa-aaaa-aaaaaaaaaaaa")
        val meeting = resolve(RawMeeting::class.java, "38e8985b-a504-4c24-bd18-0953fb593350")
        val agendaItem = resolve(RawAgendaItem::class.java, "0b30e64f-a824-4856-903f-63a81448fb70")
        val consultation = resolve(RawConsultation::class.java, "d15aff89-2aa6-4f2d-966e-8797751ceda8")

        assertNotNull(legislativeTermRepository.findByResource(legislativeTerm.resource))
        assertNotNull(paperRepository.findByResource(paper.resource))
        assertNotNull(personRepository.findByResource(person.resource))
        assertNotNull(locationRepository.findByResource(location.resource))
        assertNotNull(fileRepository.findByResource(file.resource))

        val resolvedMeeting = assertNotNull(meetingRepository.findByResource(meeting.resource))
        assertEquals(1, resolvedMeeting.organizations.size)

        val resolvedAgendaItem = assertNotNull(agendaItemRepository.findByResource(agendaItem.resource))
        assertNotNull(resolvedAgendaItem.resolutionFile)
        assertEquals(1, resolvedAgendaItem.auxiliaryFiles.size)

        val resolvedMembership = assertNotNull(membershipRepository.findByResource(membership.resource))
        assertNotNull(resolvedMembership.onBehalfOf)

        val resolvedConsultation = assertNotNull(consultationRepository.findByResource(consultation.resource))
        assertEquals(1, resolvedConsultation.organizations.size)
    }

    private fun <T : AbstractOparlEntity> resolve(type: Class<T>, id: String): T {
        val raw = entityManager.find(type, UUID.fromString(id))
        val candidate = ResolveTarget.create(raw)
        val target = targetRepository.save(
            (targetRepository.findByRawEntityIdAndEntityType(raw.id, candidate.entityType) ?: candidate).enqueue(),
        )
        resolver.run(target.id)
        assertNotNull(targetRepository.findById(target.id).orElseThrow().lastResolvedAt)
        return raw
    }
}
