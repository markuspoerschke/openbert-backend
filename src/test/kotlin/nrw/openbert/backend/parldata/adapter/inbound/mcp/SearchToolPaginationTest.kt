package nrw.openbert.backend.parldata.adapter.inbound.mcp

import io.mockk.every
import io.mockk.mockk
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListPaginationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlSearchResultMapper
import nrw.openbert.backend.parldata.adapter.inbound.mcp.dto.McpSearchResultListDto
import nrw.openbert.backend.parldata.application.port.inbound.OparlEntitySearchServiceInterface
import nrw.openbert.backend.parldata.application.port.model.SearchCriteria
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlSearchReference
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.UUID
import kotlin.test.assertEquals

class SearchToolPaginationTest {
    private val searchService = mockk<OparlEntitySearchServiceInterface>()
    private val mapper = mockk<OparlSearchResultMapper>()
    private val searchTool = SearchTool(searchService, mapper)

    @Test
    fun `search returns mapped results with default pagination`() {
        // given
        val searchReference =
            OparlSearchReference(
                entityType = OparlType.PAPER,
                entityId = UUID.randomUUID(),
            )
        val searchCriteria =
            SearchCriteria(
                term = "test",
                bodies = emptyList(),
                types = OparlType.entries.toList(),
            )
        val pageRequest = PageRequest.of(0, 30)
        val page = PageImpl(listOf(searchReference), pageRequest, 1)
        val mappedResult = mapOf("id" to "https://oparl.example.org/bodies/0/papers/1")

        every {
            searchService.search(searchCriteria, pageRequest)
        } returns page
        every {
            mapper.toDto(searchReference)
        } returns mappedResult

        // when
        val result = searchTool.search("test")

        // then
        assertEquals(
            McpSearchResultListDto(
                data = listOf(mappedResult),
                pagination =
                    OparlListPaginationDto(
                        totalElements = 1,
                        elementsPerPage = 30,
                        currentPage = 1,
                        totalPages = 1,
                    ),
            ),
            result,
        )
    }

    @Test
    fun `search with custom pagination parameters`() {
        // given
        val searchReference =
            OparlSearchReference(
                entityType = OparlType.PAPER,
                entityId = UUID.randomUUID(),
            )
        val searchCriteria =
            SearchCriteria(
                term = "test",
                bodies = emptyList(),
                types = OparlType.entries.toList(),
            )
        val pageRequest = PageRequest.of(1, 10) // Page 2, 10 items per page
        val page = PageImpl(listOf(searchReference), pageRequest, 30) // Total 30 elements, 3 pages
        val mappedResult = mapOf("id" to "https://oparl.example.org/bodies/0/papers/1")

        every {
            searchService.search(searchCriteria, pageRequest)
        } returns page
        every {
            mapper.toDto(searchReference)
        } returns mappedResult

        // when
        val result = searchTool.search("test", page = 2, pageSize = 10)

        // then
        assertEquals(
            McpSearchResultListDto(
                data = listOf(mappedResult),
                pagination =
                    OparlListPaginationDto(
                        totalElements = 30, // Total elements in the page
                        elementsPerPage = 10,
                        currentPage = 2,
                        totalPages = 3,
                    ),
            ),
            result,
        )
    }

    @Test
    fun `search with page size exceeding maximum uses maximum`() {
        // given
        val searchReference =
            OparlSearchReference(
                entityType = OparlType.PAPER,
                entityId = UUID.randomUUID(),
            )
        val searchCriteria =
            SearchCriteria(
                term = "test",
                bodies = emptyList(),
                types = OparlType.entries.toList(),
            )
        val pageRequest = PageRequest.of(0, 100) // Max page size
        val page = PageImpl(listOf(searchReference), pageRequest, 1)
        val mappedResult = mapOf("id" to "https://oparl.example.org/bodies/0/papers/1")

        every {
            searchService.search(searchCriteria, pageRequest)
        } returns page
        every {
            mapper.toDto(searchReference)
        } returns mappedResult

        // when
        val result = searchTool.search("test", pageSize = 200) // Exceeds max

        // then
        assertEquals(
            McpSearchResultListDto(
                data = listOf(mappedResult),
                pagination =
                    OparlListPaginationDto(
                        totalElements = 1,
                        elementsPerPage = 100,
                        currentPage = 1,
                        totalPages = 1,
                    ),
            ),
            result,
        )
    }
}
