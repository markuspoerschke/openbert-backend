package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import org.jobrunr.jobs.filters.JobFilter
import org.jobrunr.server.BackgroundJobServer
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class JobRunrFilterConfigurationTest {

    @Test
    fun `registers job filters on background job server bean`() {
        val filter1 = mock(JobFilter::class.java)
        val filter2 = mock(JobFilter::class.java)
        val filters = listOf(filter1, filter2)

        val config = JobRunrFilterConfiguration(filters)
        val server = mock(BackgroundJobServer::class.java)

        val result = config.postProcessAfterInitialization(server, "backgroundJobServer")

        verify(server).setJobFilters(filters)
        assert(result === server)
    }
}
