package nrw.openbert.backend.domain.oparl.entity

import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class OparlSystemTest {

    @Test
    fun `scopes imported entity IDs to their system`() {
        val firstSystem = OparlSystem(OparlUrl("https://first.example/oparl/v1.1/system"))
        val secondSystem = OparlSystem(OparlUrl("https://second.example/oparl/v1.1/system"))
        val bodyUrl = OparlUrl("https://shared.example/oparl/v1.1/body/1")

        val firstBodyId = firstSystem.entityId(bodyUrl)
        val secondBodyId = secondSystem.entityId(bodyUrl)

        assertEquals(firstSystem.id, firstBodyId.systemId)
        assertEquals(secondSystem.id, secondBodyId.systemId)
        assertNotEquals(firstBodyId, secondBodyId)
    }
}