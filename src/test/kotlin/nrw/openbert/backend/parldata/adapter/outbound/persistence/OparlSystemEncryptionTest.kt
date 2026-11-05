package nrw.openbert.backend.parldata.adapter.outbound.persistence

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.valueObject.AuthorizationHeader
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration::class)
class OparlSystemEncryptionTest(
    @Autowired private val entityManager: EntityManager,
) {
    @Test
    fun `authorization header is stored encrypted and read back decrypted`() {
        val system =
            OparlSystem(
                OparlUrl("https://oparl-encryption-test.example.org"),
                UUID.randomUUID(),
                authorizationHeader = AuthorizationHeader("Bearer secret-token"),
            )
        entityManager.persist(system)
        entityManager.flush()
        entityManager.clear()

        val raw =
            entityManager
                .createNativeQuery("SELECT authorization_header FROM oparl_system WHERE id = :id")
                .setParameter("id", system.id)
                .singleResult as String
        assertNotNull(raw)
        assertNotEquals("Bearer secret-token", raw)

        val loaded = entityManager.find(OparlSystem::class.java, system.id)
        assertEquals(AuthorizationHeader("Bearer secret-token"), loaded.authorizationHeader)
    }
}
