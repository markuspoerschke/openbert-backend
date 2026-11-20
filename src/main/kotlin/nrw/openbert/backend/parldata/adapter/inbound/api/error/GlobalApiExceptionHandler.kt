package nrw.openbert.backend.parldata.adapter.inbound.api.error

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Catch-all for unexpected exceptions: logs the real error but answers with a generic
 * RFC 7807 problem response so no internals (stack traces, class names, SQL) leak.
 *
 * All expected error cases — validation failures, type mismatches, and
 * [org.springframework.web.server.ResponseStatusException] thrown by controllers — are
 * rendered as problem details by the `ResponseEntityExceptionHandler` that Spring Boot
 * autoconfigures for `spring.mvc.problemdetails.enabled=true`, which is ordered ahead
 * of this advice.
 */
@RestControllerAdvice
class GlobalApiExceptionHandler {
    @InternalServerErrorProblem
    @ExceptionHandler(Exception::class)
    fun handleUnexpected(
        ex: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetail> {
        log.error("Unhandled exception for {} {}", request.method, request.requestURI, ex)
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.")
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problem)
    }

    private companion object {
        private val log = LoggerFactory.getLogger(GlobalApiExceptionHandler::class.java)
    }
}
