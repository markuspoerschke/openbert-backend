package nrw.openbert.backend.config

import nrw.openbert.backend.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
class SwaggerConfigTest(
    @Autowired private val mockMvc: MockMvc,
) {
    
    @Test
    fun `root path redirects to swagger docs`() {
        mockMvc.get("/")
            .andExpect {
                status { is3xxRedirection() }
                header { string("Location", "/docs") }
            }
    }
    
    @Test
    fun `swagger UI is available at docs path`() {
        mockMvc.get("/docs")
            .andExpect {
                status { is3xxRedirection() }
                header { string("Location", "/swagger-ui/index.html") }
            }
    }
    
    @Test
    fun `swagger UI is available at swagger-ui path`() {
        mockMvc.get("/swagger-ui/index.html")
            .andExpect {
                status { isOk() }
            }
    }
    
    @Test
    fun `openapi json is available at docs openapi path`() {
        mockMvc.get("/docs/openapi.json")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
            }
    }
}
