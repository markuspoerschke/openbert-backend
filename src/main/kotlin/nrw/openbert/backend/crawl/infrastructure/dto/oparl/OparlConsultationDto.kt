package nrw.openbert.backend.crawl.infrastructure.dto.oparl

data class OparlConsultationDto(
    override val id: String? = null,
    override val type: String? = null,
    val paper: String? = null,
    val agendaItem: String? = null,
    val meeting: String? = null,
    val organization: List<String>? = null,
    val authoritative: Boolean? = null,
    val role: String? = null,
    val license: String? = null,
    val keyword: List<String>? = null,
    val created: String? = null,
    val modified: String? = null,
    val web: String? = null,
    val deleted: Boolean? = null,
) : OparlObjectDto
