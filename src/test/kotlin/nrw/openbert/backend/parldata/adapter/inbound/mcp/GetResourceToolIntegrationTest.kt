package nrw.openbert.backend.parldata.adapter.inbound.mcp

import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPersonDto
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class GetResourceToolIntegrationTest(
    @Autowired private val tool: GetResourceTool,
) {
    @AfterEach
    fun cleanUp() {
        RequestContextHolder.resetRequestAttributes()
    }

    @Test
    fun `returns the full OParl DTO like the controller does`() {
        RequestContextHolder.setRequestAttributes(
            ServletRequestAttributes(MockHttpServletRequest()),
        )

        val result = tool.getResource(
            "https://example.org/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3" +
                "/person/f6d47f4c-e5db-4dab-91f8-3bafc825d30c",
        )

        assertTrue(result is OparlPersonDto)
        val person = result as OparlPersonDto
        assertEquals(
            "http://localhost/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3" +
                "/person/f6d47f4c-e5db-4dab-91f8-3bafc825d30c",
            person.id,
        )
        assertEquals("https://schema.oparl.org/1.1/Person", person.type)
        assertEquals("Mustermann", person.familyName)
    }
}
