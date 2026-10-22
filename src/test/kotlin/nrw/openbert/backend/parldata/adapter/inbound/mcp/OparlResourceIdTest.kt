package nrw.openbert.backend.parldata.adapter.inbound.mcp

import nrw.openbert.backend.parldata.domain.enums.OparlType
import java.util.UUID
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class OparlResourceIdTest {
    @Test
    fun `parses body url`() {
        val result = OparlResourceId.fromUrl(
            "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3",
        )

        assertEquals(OparlType.BODY, result?.type)
        assertEquals(UUID.fromString("903c26aa-dc8f-4d34-afd6-afdc5040dbd3"), result?.id)
    }

    @Test
    fun `parses resource url of every entity type`() {
        val entityNames = mapOf(
            "agendaitem" to OparlType.AGENDA_ITEM,
            "consultation" to OparlType.CONSULTATION,
            "file" to OparlType.FILE,
            "legislativeterm" to OparlType.LEGISLATIVE_TERM,
            "location" to OparlType.LOCATION,
            "meeting" to OparlType.MEETING,
            "membership" to OparlType.MEMBERSHIP,
            "organization" to OparlType.ORGANIZATION,
            "paper" to OparlType.PAPER,
            "person" to OparlType.PERSON,
        )

        entityNames.forEach { (name, type) ->
            val url = "https://example.org/oparl/v1.1/body" +
                "/903c26aa-dc8f-4d34-afd6-afdc5040dbd3/$name/11111111-1111-4111-a111-111111111111"
            val result = OparlResourceId.fromUrl(url)

            assertEquals(type, result?.type, "Failed to parse entity name $name")
            assertEquals(UUID.fromString("11111111-1111-4111-a111-111111111111"), result?.id)
        }
    }

    @Test
    fun `accepts urls with trailing path segments`() {
        val url = "https://example.org/oparl/v1.1/body" +
            "/903c26aa-dc8f-4d34-afd6-afdc5040dbd3/file/22222222-2222-4222-a222-222222222222/download"
        val result = OparlResourceId.fromUrl(url)

        assertEquals(OparlType.FILE, result?.type)
        assertEquals(UUID.fromString("22222222-2222-4222-a222-222222222222"), result?.id)
    }

    @Test
    fun `rejects invalid urls`() {
        assertNull(OparlResourceId.fromUrl("not a url"))
        assertNull(OparlResourceId.fromUrl("https://example.org/oparl/v1.1/body/not-a-uuid"))
        assertNull(OparlResourceId.fromUrl("https://example.org/oparl/v1.1/body"))
        assertNull(
            OparlResourceId.fromUrl(
                "https://example.org/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3/" +
                    "system/11111111-1111-4111-a111-111111111111",
            ),
        )
    }
}
