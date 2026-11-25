package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlBodyStatisticsDto
import nrw.openbert.backend.parldata.adapter.inbound.api.error.BadRequestProblem
import nrw.openbert.backend.parldata.adapter.inbound.api.error.NotFoundProblem
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Tag(name = "Statistics")
@RestController
class OparlBodyStatisticsController(
    private val resolvedBodyRepository: ResolvedBodyRepository,
) {
    @Operation(
        summary = "Resource statistics for the body",
        description = "Counts resolved, non-deleted records in the body's default OParl resource lists.",
    )
    @ApiResponse(responseCode = "200", description = "Record counts by OParl resource type", useReturnTypeSchema = true)
    @BadRequestProblem
    @NotFoundProblem
    @GetMapping("oparl/v1.1/body/{bodyId}/statistics")
    fun getStatistics(
        @Parameter(description = "Openbert OParl body UUID") @PathVariable bodyId: UUID,
    ): OparlBodyStatisticsDto {
        val counts =
            resolvedBodyRepository.findResourceCounts(bodyId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Body $bodyId does not exist.")

        return OparlBodyStatisticsDto(
            persons = counts.persons,
            organizations = counts.organizations,
            paper = counts.papers,
            files = counts.files,
            meetings = counts.meetings,
            agendaItems = counts.agendaItems,
            locations = counts.locations,
            consultations = counts.consultations,
            legislativeTerms = counts.legislativeTerms,
            memberships = counts.memberships,
        )
    }
}
