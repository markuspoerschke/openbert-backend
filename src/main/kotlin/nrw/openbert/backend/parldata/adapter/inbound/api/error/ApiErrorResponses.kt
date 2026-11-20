package nrw.openbert.backend.parldata.adapter.inbound.api.error

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail

/**
 * Documents an RFC 7807 `application/problem+json` response with status 400
 * (invalid request parameters, e.g. pagination or search text).
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(
    responseCode = "400",
    description = "Invalid request parameters",
    content = [
        Content(
            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
            schema = Schema(implementation = ProblemDetail::class),
        ),
    ],
)
annotation class BadRequestProblem

/**
 * Documents an RFC 7807 `application/problem+json` response with status 404
 * (unknown body or resource).
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(
    responseCode = "404",
    description = "Resource not found",
    content = [
        Content(
            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
            schema = Schema(implementation = ProblemDetail::class),
        ),
    ],
)
annotation class NotFoundProblem

/**
 * Documents an RFC 7807 `application/problem+json` response with status 502
 * (upstream OParl server could not be fetched).
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(
    responseCode = "502",
    description = "Upstream OParl server error",
    content = [
        Content(
            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
            schema = Schema(implementation = ProblemDetail::class),
        ),
    ],
)
annotation class BadGatewayProblem

/**
 * Documents an RFC 7807 `application/problem+json` response with status 500.
 * Placed on the catch-all `@ExceptionHandler` of [GlobalApiExceptionHandler]; springdoc
 * adds controller-advice responses to every operation, so every endpoint gets a 500.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(
    responseCode = "500",
    description = "Unexpected server error",
    content = [
        Content(
            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
            schema = Schema(implementation = ProblemDetail::class),
        ),
    ],
)
annotation class InternalServerErrorProblem
