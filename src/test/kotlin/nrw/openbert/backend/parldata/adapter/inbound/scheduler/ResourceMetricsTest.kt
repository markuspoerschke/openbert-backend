package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import io.micrometer.core.instrument.MeterRegistry
import nrw.openbert.backend.TestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class ResourceMetricsTest(
    @Autowired private val resourceMetrics: ResourceMetrics,
    @Autowired private val registry: MeterRegistry,
) {
    @Test
    fun `publishes the stored resolved count for every OParl type`() {
        resourceMetrics.refresh()

        val counts =
            registry
                .find("oparl.resources")
                .gauges()
                .associate { it.id.getTag("resource") to it.value() }

        assertThat(counts).containsExactlyInAnyOrderEntriesOf(
            mapOf(
                "agenda_item" to 2.0,
                "body" to 2.0,
                "consultation" to 1.0,
                "file" to 4.0,
                "legislative_term" to 1.0,
                "location" to 1.0,
                "meeting" to 1.0,
                "membership" to 1.0,
                "organization" to 2.0,
                "paper" to 1.0,
                "person" to 1.0,
                "system" to 2.0,
            ),
        )
    }
}
