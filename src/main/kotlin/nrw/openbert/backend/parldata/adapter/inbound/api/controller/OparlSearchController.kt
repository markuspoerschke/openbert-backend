package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListLinksDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListPaginationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlSearchResultListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlSearchResultMapper
import nrw.openbert.backend.parldata.application.port.inbound.OparlEntitySearchServiceInterface
import nrw.openbert.backend.parldata.application.port.model.SearchCriteria
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Tag(name = "Search", externalDocs = ExternalDocumentation("Oparl Specs", "https://oparl.org/spezifikation/online-ansicht/"))
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
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<OparlSearchResultListDto> {
        val searchText = text?.trim()
        val invalidText = searchText != null && !SEARCHABLE_TEXT.containsMatchIn(searchText)
        if (invalidText || page < 1 || size !in 1..MAX_PAGE_SIZE) {
            return ResponseEntity.badRequest().build()
        }

        // Convert string types to OparlType enum values
        val typeEnums = types?.let { typeStrings ->
            typeStrings.mapNotNull { typeString ->
                OparlType.entries.find { it.name == typeString.uppercase() }
            }
        } ?: OparlType.entries.toList()

        val resultPage = searchService.search(
            SearchCriteria(
                term = searchText,
                bodies = bodies ?: emptyList(),
                types = typeEnums
            ),
            PageRequest.of(page - 1, size),
        ).map(mapper::toDto)

        val baseUrl = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .build()
            .toUriString()
        val totalPages = resultPage.totalPages

        return ResponseEntity.ok(
            OparlSearchResultListDto(
                id = baseUrl,
                data = resultPage.content,
                pagination = OparlListPaginationDto(
                    totalElements = Math.toIntExact(resultPage.totalElements),
                    elementsPerPage = size,
                    currentPage = page,
                    totalPages = totalPages,
                ),
                links = OparlListLinksDto(
                    first = pageUrl(baseUrl, searchText, bodies, types, 1, size),
                    prev = if (page > 1) pageUrl(baseUrl, searchText, bodies, types, page - 1, size) else null,
                    self = pageUrl(baseUrl, searchText, bodies, types, page, size),
                    next = if (page < totalPages) {
                        pageUrl(baseUrl, searchText, bodies, types, page + 1, size)
                    } else {
                        null
                    },
                    last = pageUrl(baseUrl, searchText, bodies, types, totalPages.coerceAtLeast(1), size),
                ),
            )
        )
    }

    private fun pageUrl(
        baseUrl: String,
        text: String?,
        bodies: List<String>?,
        types: List<String>?,
        page: Int,
        size: Int,
    ): String {
        val builder = ServletUriComponentsBuilder
            .fromUriString(baseUrl)
            .queryParam("page", page)
            .queryParam("size", size)
        
        text?.let { builder.queryParam("text", it) }
        bodies?.let { if (it.isNotEmpty()) builder.queryParam("bodies", it.joinToString(",")) }
        types?.let { if (it.isNotEmpty()) builder.queryParam("types", it.joinToString(",")) }
        
        return builder.build()
            .encode()
            .toUriString()
    }

    companion object {
        // Patterns without a complete trigram would scan the whole search index.
        private val SEARCHABLE_TEXT = Regex("[\\p{L}\\p{Nd}]{3}")
        private const val MAX_PAGE_SIZE = 100
    }
}
