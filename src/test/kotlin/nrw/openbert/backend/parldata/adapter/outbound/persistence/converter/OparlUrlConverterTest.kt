package nrw.openbert.backend.parldata.adapter.outbound.persistence.converter

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawMeetingRepository
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawMeeting
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedFile
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class OparlUrlConverterTest(
    @Autowired private val entityManager: EntityManager,
    @Autowired private val rawMeetingRepository: RawMeetingRepository,
) {
    @Test
    fun `automatically converts direct embedded inherited and raw URL attributes`() {
        val system = entityManager.find(OparlSystem::class.java, SYSTEM_ID)
        assertEquals(OparlUrl("https://oparl-demo.example.org"), system.url)

        val resource =
            OparlResource(
                system,
                OparlUrl("https://oparl-demo.example.org/oparl/v1.1/meeting/body-only"),
            )
        val meeting = rawMeetingRepository.findByResource(resource)
        assertNotNull(meeting)
        assertEquals(resource, meeting.resource)
        assertEquals(OparlUrl("https://oparl-demo.example.org/oparl/v1.1/body/123"), meeting.bodyUrl)

        val file = entityManager.find(ResolvedFile::class.java, FILE_ID)
        assertEquals(OparlUrl("https://oparl-demo.example.org/files/1"), file.accessUrl)
        assertEquals(OparlUrl("https://oparl-demo.example.org/files/download/1"), file.downloadUrl)
        assertNull(file.externalServiceUrl)
    }

    @Test
    fun `automatically converts URL updates and null values`() {
        val meeting = entityManager.find(RawMeeting::class.java, MEETING_ID)
        val file = entityManager.find(ResolvedFile::class.java, FILE_ID)
        val bodyUrl = meeting.bodyUrl
        val accessUrl = file.accessUrl
        assertNotNull(bodyUrl)
        assertNotNull(accessUrl)

        meeting.bodyUrl = accessUrl
        file.externalServiceUrl = bodyUrl
        file.downloadUrl = null
        entityManager.flush()
        entityManager.clear()

        val reloadedMeeting = entityManager.find(RawMeeting::class.java, MEETING_ID)
        val reloadedFile = entityManager.find(ResolvedFile::class.java, FILE_ID)
        assertEquals(accessUrl, reloadedMeeting.bodyUrl)
        assertEquals(bodyUrl, reloadedFile.externalServiceUrl)
        assertNull(reloadedFile.downloadUrl)
    }

    companion object {
        private val SYSTEM_ID = UUID.fromString("9352fa21-68c6-4f4e-8832-08a0454ff179")
        private val MEETING_ID = UUID.fromString("10000000-0000-4000-8000-000000000007")
        private val FILE_ID = UUID.fromString("3d663f77-05ca-4a14-b788-68ad340ea271")
    }
}
