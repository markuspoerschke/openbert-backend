package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListLinksDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListPaginationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlSearchResultListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlSearchResultMapper
import nrw.openbert.backend.parldata.application.port.inbound.OparlEntitySearchServiceInterface
import nrw.openbert.backend.parldata.application.port.model.SearchCriteria
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Tag(
    name = "Search",
    externalDocs =
        ExternalDocumentation(
            "Oparl Specs",
            "https://oparl.org/spezifikation/online-ansicht/",
        ),
)
@RestController
class OparlSearchController(
    private val searchService: OparlEntitySearchServiceInterface,
    private val mapper: OparlSearchResultMapper,
) {
    @GetMapping("oparl/v1.1/search")
    fun search(
        @Parameter(description = "Literal substring containing at least three consecutive letters or digits")
        @RequestParam(required = false) text: String?,
        @Parameter(description = "List of body IDs to filter by")
        @RequestParam(required = false) bodies: List<String>?,
        @Parameter(description = "List of OParl types to filter by (e.g., PERSON, ORGANIZATION)")
        @RequestParam(required = false) types: List<String>?,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlSearchResultListDto> {
        val searchText = text?.trim()
        val invalidText = searchText != null && !SEARCHABLE_TEXT.containsMatchIn(searchText)
        if (invalidText) {
            return ResponseEntity.badRequest().build()
        }

        val typeEnums = resolveTypes(types)

        val resultPage =
            searchService
                .search(
                    SearchCriteria(
                        term = searchText,
                        bodies = bodies ?: emptyList(),
                        types = typeEnums,
                    ),
                    pagination.toSpringPageRequest(),
                ).map(mapper::toDto)

        val baseUrl =
            ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .build()
                .toUriString()
        val totalPages = resultPage.totalPages

        return ResponseEntity.ok(
            OparlSearchResultListDto(
                id = baseUrl,
                data = resultPage.content,
                pagination =
                    OparlListPaginationDto(
                        totalElements = Math.toIntExact(resultPage.totalElements),
                        elementsPerPage = pagination.size,
                        currentPage = pagination.page,
                        totalPages = totalPages,
                    ),
                links =
                    createLinks(
                        baseUrl = baseUrl,
                        searchText = searchText,
                        bodies = bodies,
                        types = types,
                        currentPage = pagination.page,
                        totalPages = totalPages,
                        size = pagination.size,
                    ),
            ),
        )
    }

    private fun resolveTypes(types: List<String>?): List<OparlType> =
        types?.let { typeStrings ->
            typeStrings.mapNotNull { typeString ->
                OparlType.entries.find { it.name == typeString.uppercase() }
            }
        } ?: OparlType.entries.toList()

    private fun createLinks(
        baseUrl: String,
        searchText: String?,
        bodies: List<String>?,
        types: List<String>?,
        currentPage: Int,
        totalPages: Int,
        size: Int,
    ): OparlListLinksDto =
        OparlListLinksDto(
            first = pageUrl(baseUrl, searchText, bodies, types, 1, size),
            prev = if (currentPage > 1) pageUrl(baseUrl, searchText, bodies, types, currentPage - 1, size) else null,
            self = pageUrl(baseUrl, searchText, bodies, types, currentPage, size),
            next =
                if (currentPage <
                    totalPages
                ) {
                    pageUrl(baseUrl, searchText, bodies, types, currentPage + 1, size)
                } else {
                    null
                },
            last = pageUrl(baseUrl, searchText, bodies, types, totalPages.coerceAtLeast(1), size),
        )

    private fun pageUrl(
        baseUrl: String,
        text: String?,
        bodies: List<String>?,
        types: List<String>?,
        page: Int,
        size: Int,
    ): String {
        val builder =
            ServletUriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("page", page)
                .queryParam("size", size)

        text?.let { builder.queryParam("text", it) }
        bodies?.let { if (it.isNotEmpty()) builder.queryParam("bodies", it.joinToString(",")) }
        types?.let { if (it.isNotEmpty()) builder.queryParam("types", it.joinToString(",")) }

        return builder
            .build()
            .encode()
            .toUriString()
    }

    companion object {
        // Patterns without a complete trigram would scan the whole search index.
        private val SEARCHABLE_TEXT = Regex("[\\p{L}\\p{Nd}]{3}")
    }
}
