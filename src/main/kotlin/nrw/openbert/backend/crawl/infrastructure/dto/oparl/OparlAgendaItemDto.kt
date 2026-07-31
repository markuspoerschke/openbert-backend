package nrw.openbert.backend.crawl.infrastructure.dto.oparl

data class OparlAgendaItemDto(
    override val id: String? = null,
    override val type: String? = null,
    val meeting: String? = null,
    val number: String? = null,
    val order: Int? = null,
    val name: String? = null,
    val public: Boolean? = null,
    val consultation: String? = null,
    val result: String? = null,
    val resolutionText: String? = null,
    val resolutionFile: OparlFileDto? = null,
    val auxiliaryFile: List<OparlFileDto>? = null,
    val start: String? = null,
    val end: String? = null,
    val license: String? = null,
    val keyword: List<String>? = null,
    val created: String? = null,
    val modified: String? = null,
    val web: String? = null,
    val deleted: Boolean? = null,
) : OparlObjectDto
