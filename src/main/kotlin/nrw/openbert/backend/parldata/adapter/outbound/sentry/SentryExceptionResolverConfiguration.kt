package nrw.openbert.backend.parldata.adapter.outbound.sentry

import io.sentry.IScopes
import io.sentry.spring.SentryExceptionResolver
import io.sentry.spring.tracing.TransactionNameProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.ErrorResponse
import org.springframework.web.servlet.ModelAndView

@Configuration
class SentryExceptionResolverConfiguration {

    @Bean
    fun sentryExceptionResolver(
        scopes: IScopes,
        transactionNameProvider: TransactionNameProvider,
        @Value("\${sentry.exception-resolver-order:-2147483647}") exceptionResolverOrder: Int,
    ): SentryExceptionResolver =
        object : SentryExceptionResolver(
            scopes,
            transactionNameProvider,
            exceptionResolverOrder,
        ) {
            override fun resolveException(
                request: HttpServletRequest,
                response: HttpServletResponse,
                handler: Any?,
                ex: Exception,
            ): ModelAndView? {
                if (isClientError(ex)) {
                    return null
                }
                return super.resolveException(request, response, handler, ex)
            }

            private fun isClientError(ex: Exception): Boolean {
                if (ex !is ErrorResponse) {
                    return false
                }
                val statusCode = ex.statusCode.value()
                return statusCode in 400..499
            }
        }
}
