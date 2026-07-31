package nrw.openbert.backend.crawl.infrastructure.dto.oparl

data class OparlLegislativeTermDto(
    override val id: String? = null,
    override val type: String? = null,
    val body: String? = null,
    val name: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val license: String? = null,
    val keyword: List<String>? = null,
    val created: String? = null,
    val modified: String? = null,
    val web: String? = null,
    val deleted: Boolean? = null,
) : OparlObjectDto
