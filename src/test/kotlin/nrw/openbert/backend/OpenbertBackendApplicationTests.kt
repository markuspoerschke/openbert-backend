package nrw.openbert.backend

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import kotlin.test.assertNotNull

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class OpenbertBackendApplicationTests(
    @Autowired private val context: ApplicationContext,
) {

    @Test
    fun contextLoads() {
        assertNotNull(context.getBean(OpenbertBackendApplication::class.java))
    }

}
