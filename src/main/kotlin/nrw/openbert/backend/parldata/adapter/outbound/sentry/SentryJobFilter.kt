package nrw.openbert.backend.parldata.adapter.outbound.sentry

import io.sentry.IScopes
import io.sentry.Sentry
import org.jobrunr.jobs.Job
import org.jobrunr.jobs.filters.JobServerFilter
import org.springframework.stereotype.Component
import java.lang.reflect.InvocationTargetException

@Component
class SentryJobFilter(
    private val scopes: IScopes = Sentry.getCurrentScopes(),
) : JobServerFilter {

    override fun onProcessingFailed(job: Job, e: Exception) {
        val actualException = unwrap(e)
        scopes.captureException(actualException) { scope ->
            scope.setTag("jobrunr.job.id", job.id.toString())
            scope.setTag("jobrunr.job.name", job.jobName)
            job.jobDetails?.let { details ->
                scope.setExtra("jobrunr.job.className", details.className)
                scope.setExtra("jobrunr.job.methodName", details.methodName)
            }
        }
    }

    private fun unwrap(throwable: Throwable): Throwable {
        var current = throwable
        while (current.cause != null && (current is InvocationTargetException || current.javaClass.name.endsWith("JobExecutionException"))) {
            current = current.cause!!
        }
        return current
    }
}
