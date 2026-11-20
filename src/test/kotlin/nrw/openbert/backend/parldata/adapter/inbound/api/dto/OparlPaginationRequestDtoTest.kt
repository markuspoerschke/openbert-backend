package nrw.openbert.backend.parldata.adapter.inbound.api.dto

import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort

class OparlPaginationRequestDtoTest {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `defaults to the first page and default page size`() {
        val dto = OparlPaginationRequestDto()

        assertEquals(1, dto.page)
        assertEquals(OparlPaginationRequestDto.PAGE_SIZE, dto.size)
        assertTrue(validator.validate(dto).isEmpty())
    }

    @Test
    fun `rejects page numbers below 1`() {
        assertEquals(
            "page",
            validator
                .validate(OparlPaginationRequestDto(page = 0))
                .single()
                .propertyPath
                .toString(),
        )
        assertEquals(
            "page",
            validator
                .validate(OparlPaginationRequestDto(page = -1))
                .single()
                .propertyPath
                .toString(),
        )
    }

    @Test
    fun `rejects size below 1`() {
        assertEquals(
            "size",
            validator
                .validate(OparlPaginationRequestDto(size = 0))
                .single()
                .propertyPath
                .toString(),
        )
        assertEquals(
            "size",
            validator
                .validate(OparlPaginationRequestDto(size = -1))
                .single()
                .propertyPath
                .toString(),
        )
    }

    @Test
    fun `rejects size above maximum page size`() {
        assertEquals(
            "size",
            validator
                .validate(OparlPaginationRequestDto(size = 101))
                .single()
                .propertyPath
                .toString(),
        )
    }

    @Test
    fun `converts the 1-based oparl page to a 0-based pageable`() {
        val pageable = OparlPaginationRequestDto(page = 3).toSpringPageRequest()

        assertEquals(2, pageable.pageNumber)
        assertEquals(OparlPaginationRequestDto.PAGE_SIZE, pageable.pageSize)
    }

    @Test
    fun `converts with custom size to spring page request`() {
        val pageable = OparlPaginationRequestDto(page = 2, size = 10).toSpringPageRequest()

        assertEquals(1, pageable.pageNumber)
        assertEquals(10, pageable.pageSize)
    }

    @Test
    fun `sorts by id descending by default so the newest entries come first`() {
        val orders = OparlPaginationRequestDto(page = 1).toSpringPageRequest().sort.toList()

        assertEquals(listOf("id"), orders.map { it.property })
        assertEquals(Sort.Direction.DESC, orders.single().direction)
    }

    @Test
    fun `parses a custom sort and keeps the unique id as tiebreaker for stable pages`() {
        val orders = OparlPaginationRequestDto(sort = "created,desc").toSpringPageRequest().sort.toList()

        assertEquals(listOf("created", "id"), orders.map { it.property })
        assertEquals(Sort.Direction.DESC, orders[0].direction)
        assertEquals(Sort.Direction.ASC, orders[1].direction)
    }

    @Test
    fun `defaults the direction to ascending and skips the tiebreaker when sorting by id`() {
        val orders = OparlPaginationRequestDto(sort = "modified").toSpringPageRequest().sort.toList()

        assertEquals(listOf("modified", "id"), orders.map { it.property })
        assertEquals(Sort.Direction.ASC, orders[0].direction)

        val byId = OparlPaginationRequestDto(sort = "id,asc").toSpringPageRequest().sort.toList()
        assertEquals(1, byId.size)
    }

    @Test
    fun `rejects unsupported sort fields`() {
        assertEquals(
            "sortValid",
            validator
                .validate(OparlPaginationRequestDto(sort = "name"))
                .single()
                .propertyPath
                .toString(),
        )
        assertTrue(validator.validate(OparlPaginationRequestDto(sort = "created,desc")).isEmpty())
        assertTrue(validator.validate(OparlPaginationRequestDto()).isEmpty())
    }
}
