package nrw.openbert.backend.parldata.adapter.inbound.mcp

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlBodyDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlSearchResultMapper
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlSearchReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.UUID

class GetResourceToolTest {
    private val mapper = mock(OparlSearchResultMapper::class.java)
    private val tool = GetResourceTool(mapper)

    @Test
    fun `returns resource for a body id`() {
        val reference =
            OparlSearchReference(
                OparlType.BODY,
                UUID.fromString("903c26aa-dc8f-4d34-afd6-afdc5040dbd3"),
            )
        val body = bodyDto()
        `when`(mapper.toDto(reference)).thenReturn(body)

        val result =
            tool.getResource(
                "https://example.org/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3",
            )

        assertEquals(body, result)
        verify(mapper).toDto(reference)
    }

    @Test
    fun `returns resource for a person id`() {
        val reference =
            OparlSearchReference(
                OparlType.PERSON,
                UUID.fromString("f6d47f4c-e5db-4dab-91f8-3bafc825d30c"),
            )
        val body = bodyDto()
        `when`(mapper.toDto(reference)).thenReturn(body)

        val result =
            tool.getResource(
                "https://example.org/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3" +
                    "/person/f6d47f4c-e5db-4dab-91f8-3bafc825d30c",
            )

        assertEquals(body, result)
        verify(mapper).toDto(reference)
    }

    @Test
    fun `returns a client friendly error message for an unsupported id`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                tool.getResource("https://example.org/not-oparl")
            }

        val message = requireNotNull(exception.message)
        assertTrue(message.startsWith("Unsupported OParl resource id 'https://example.org/not-oparl'."))
        assertTrue(message.contains("https://example.org/oparl/v1.1/body/{bodyId}/person/{personId}"))
        assertTrue(message.contains("Supported resources are bodies"))
        assertTrue(message.contains("agendaitem, consultation, file, legislativeterm, location, meeting"))
        assertTrue(message.contains("membership, organization, paper, and person"))
    }

    private fun bodyDto() =
        OparlBodyDto(
            id = "https://example.org/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3",
            type = "https://schema.oparl.org/1.1/Body",
            name = "Neu Offenstadt – Stadtparlament",
            organization = "https://example.org/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3/organization",
            person = "https://example.org/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3/person",
            meeting = "https://example.org/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3/meeting",
            paper = "https://example.org/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3/paper",
            legislativeTerm = emptyList(),
        )
}
