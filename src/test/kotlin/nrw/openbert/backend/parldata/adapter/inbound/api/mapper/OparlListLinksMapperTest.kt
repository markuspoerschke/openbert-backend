package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class OparlListLinksMapperTest {
    private val mapper = OparlListLinksMapper()

    @Test
    fun `maps links correctly for empty page`() {
        val emptyPage = PageImpl(emptyList<String>(), PageRequest.of(0, 25), 0)
        val links = mapper.toDto(emptyPage) { page -> "https://example.org/list?page=$page" }

        assertEquals("https://example.org/list?page=1", links.first)
        assertNull(links.prev)
        assertEquals("https://example.org/list?page=1", links.self)
        assertNull(links.next)
        assertEquals("https://example.org/list?page=1", links.last)
    }

    @Test
    fun `maps links correctly for multi-page list`() {
        val page = PageImpl(listOf("a", "b"), PageRequest.of(1, 2), 6)
        val links = mapper.toDto(page) { pageNumber -> "https://example.org/list?page=$pageNumber" }

        assertEquals("https://example.org/list?page=1", links.first)
        assertEquals("https://example.org/list?page=1", links.prev)
        assertEquals("https://example.org/list?page=2", links.self)
        assertEquals("https://example.org/list?page=3", links.next)
        assertEquals("https://example.org/list?page=3", links.last)
    }
}
