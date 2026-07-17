package nrw.openbert.backend.domain.oparl.entity

import nrw.openbert.backend.domain.oparl.entity.raw.RawBody
import nrw.openbert.backend.domain.oparl.entity.resolved.ResolvedBody
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class OparlSystemTest {

    @Test
    fun `scopes imported entities to their system`() {
        val firstSystem = OparlSystem(OparlUrl("https://first.example/oparl/v1.1/system"))
        val secondSystem = OparlSystem(OparlUrl("https://second.example/oparl/v1.1/system"))
        val bodyUrl = OparlUrl("https://shared.example/oparl/v1.1/body/1")

        val firstBody = RawBody(firstSystem.id, bodyUrl)
        val secondBody = RawBody(secondSystem.id, bodyUrl)

        assertEquals(firstSystem.id, firstBody.systemId)
        assertEquals(secondSystem.id, secondBody.systemId)
        assertEquals(bodyUrl, firstBody.url)
        assertEquals(bodyUrl, secondBody.url)
        assertNotEquals(firstBody.id, secondBody.id)
    }

    @Test
    fun `resolved entity keeps source identity with its own UUID`() {
        val system = OparlSystem(OparlUrl("https://example.com/oparl/v1.1/system"))
        val raw = RawBody(system.id, OparlUrl("https://example.com/oparl/v1.1/body/1"))

        val resolved = ResolvedBody.fromRaw(raw)

        assertEquals(raw.systemId, resolved.systemId)
        assertEquals(raw.url, resolved.url)
        assertNotEquals(raw.id, resolved.id)
    }
}