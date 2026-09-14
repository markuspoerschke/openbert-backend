package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import io.swagger.v3.oas.annotations.Parameter
import nrw.openbert.backend.oparl.application.OparlEntitySearchService
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListLinksDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListPaginationDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlSearchResultListDto
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
class OparlSearchController(
    private val searchService: OparlEntitySearchService,
) {
    @GetMapping("oparl/v1.1/search")
    fun search(
        @Parameter(description = "Literal substring containing at least three consecutive letters or digits")
        @RequestParam text: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<OparlSearchResultListDto> {
        val searchText = text.trim()
        if (!SEARCHABLE_TEXT.containsMatchIn(searchText) || page < 1 || size !in 1..MAX_PAGE_SIZE) {
            return ResponseEntity.badRequest().build()
        }

        val resultPage = searchService.search(
            searchText,
            PageRequest.of(page - 1, size),
        )

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
                    first = pageUrl(baseUrl, searchText, 1, size),
                    prev = if (page > 1) pageUrl(baseUrl, searchText, page - 1, size) else null,
                    self = pageUrl(baseUrl, searchText, page, size),
                    next = if (page < totalPages) {
                        pageUrl(baseUrl, searchText, page + 1, size)
                    } else {
                        null
                    },
                    last = pageUrl(baseUrl, searchText, totalPages.coerceAtLeast(1), size),
                ),
            )
        )
    }

    private fun pageUrl(baseUrl: String, text: String, page: Int, size: Int): String {
        return ServletUriComponentsBuilder
            .fromUriString(baseUrl)
            .queryParam("text", text)
            .queryParam("page", page)
            .queryParam("size", size)
            .build()
            .encode()
            .toUriString()
    }

    companion object {
        // Patterns without a complete trigram would scan the whole search index.
        private val SEARCHABLE_TEXT = Regex("[\\p{L}\\p{Nd}]{3}")
        private const val MAX_PAGE_SIZE = 100
    }
}
