package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import org.jobrunr.jobs.filters.JobFilter
import org.jobrunr.server.BackgroundJobServer
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Configuration

@Configuration
class JobRunrFilterConfiguration(
    private val jobFilters: List<JobFilter>,
) : BeanPostProcessor {

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean is BackgroundJobServer) {
            bean.setJobFilters(jobFilters)
        }
        return bean
    }
}
