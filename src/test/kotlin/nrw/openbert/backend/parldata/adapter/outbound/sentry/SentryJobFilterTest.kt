package nrw.openbert.backend.parldata.adapter.outbound.sentry

import io.sentry.IScopes
import io.sentry.IScope
import io.sentry.ScopeCallback
import org.jobrunr.jobs.Job
import org.jobrunr.jobs.JobDetails
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.lang.reflect.InvocationTargetException
import java.util.UUID
import kotlin.test.assertEquals

class SentryJobFilterTest {

    private val scopes = mock(IScopes::class.java)
    private val filter = SentryJobFilter(scopes)

    @Test
    fun `captures exception and sets job tags and details on scope`() {
        val jobId = UUID.randomUUID()
        val job = mock(Job::class.java)
        val jobDetails = mock(JobDetails::class.java)
        `when`(jobDetails.className).thenReturn("com.example.TestService")
        `when`(jobDetails.methodName).thenReturn("processItem")
        `when`(job.id).thenReturn(jobId)
        `when`(job.jobName).thenReturn("TestJobName")
        `when`(job.jobDetails).thenReturn(jobDetails)

        val rootException = RuntimeException("Something failed")
        val wrappedException = InvocationTargetException(rootException)

        filter.onProcessingFailed(job, wrappedException)

        val exceptionCaptor = ArgumentCaptor.forClass(Throwable::class.java)
        val callbackCaptor = ArgumentCaptor.forClass(ScopeCallback::class.java)

        verify(scopes).captureException(exceptionCaptor.capture(), callbackCaptor.capture())

        assertEquals(rootException, exceptionCaptor.value)

        val scope = mock(IScope::class.java)
        callbackCaptor.value.run(scope)

        verify(scope).setTag("jobrunr.job.id", jobId.toString())
        verify(scope).setTag("jobrunr.job.name", "TestJobName")
        verify(scope).setExtra("jobrunr.job.className", "com.example.TestService")
        verify(scope).setExtra("jobrunr.job.methodName", "processItem")
    }
}
