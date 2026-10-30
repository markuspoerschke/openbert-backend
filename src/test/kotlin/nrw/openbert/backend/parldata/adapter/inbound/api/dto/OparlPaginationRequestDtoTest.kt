package nrw.openbert.backend.parldata.adapter.inbound.api.dto

import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

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
}
