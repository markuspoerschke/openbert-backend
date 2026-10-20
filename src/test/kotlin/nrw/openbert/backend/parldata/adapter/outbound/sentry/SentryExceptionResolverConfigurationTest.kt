package nrw.openbert.backend.parldata.adapter.outbound.sentry

import io.sentry.IScopes
import io.sentry.SentryOptions
import io.sentry.spring.SentryExceptionResolver
import io.sentry.spring.tracing.TransactionNameProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.ErrorResponse
import org.springframework.web.servlet.resource.NoResourceFoundException
import kotlin.test.assertNull

class SentryExceptionResolverConfigurationTest {

    private val scopes = mock(IScopes::class.java)
    private val transactionNameProvider = mock(TransactionNameProvider::class.java)
    private val request = mock(HttpServletRequest::class.java)
    private val response = mock(HttpServletResponse::class.java)

    private lateinit var resolver: SentryExceptionResolver

    @BeforeEach
    fun setUp() {
        `when`(scopes.options).thenReturn(SentryOptions())
        resolver = SentryExceptionResolverConfiguration()
            .sentryExceptionResolver(scopes, transactionNameProvider, Int.MIN_VALUE)
    }

    @Test
    fun `does not capture exceptions that resolve to a 4xx client error`() {
        val exception = NoResourceFoundException(HttpMethod.GET, "/favicon.ico", "favicon.ico")

        val result = resolver.resolveException(request, response, null, exception)

        assertNull(result)
        verify(scopes, never()).captureEvent(any(), any())
    }

    @Test
    fun `captures exceptions that are not an ErrorResponse`() {
        val exception = IllegalStateException("something failed")

        val result = resolver.resolveException(request, response, null, exception)

        assertNull(result)
        verify(scopes).captureEvent(any(), any())
    }

    @Test
    fun `captures ErrorResponse exceptions with a non-4xx status`() {
        val exception = object : ErrorResponse {
            override fun getStatusCode() = HttpStatus.INTERNAL_SERVER_ERROR
            override fun getBody() = throw NotImplementedError()
        }

        resolver.resolveException(request, response, null, exception)

        verify(scopes).captureEvent(any(), any())
    }
}
