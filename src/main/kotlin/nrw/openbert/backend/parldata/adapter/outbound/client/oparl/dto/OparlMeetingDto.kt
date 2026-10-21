package nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto

data class OparlMeetingDto(
    override val id: String? = null,
    override val type: String? = null,
    // Some providers expose the owning body as an extension to OParl Meeting.
    val body: String? = null,
    val name: String? = null,
    val meetingState: String? = null,
    val cancelled: Boolean? = null,
    val start: String? = null,
    val end: String? = null,
    val location: OparlLocationDto? = null,
    val organization: List<String>? = null,
    val participant: List<String>? = null,
    val invitation: OparlFileDto? = null,
    val resultsProtocol: OparlFileDto? = null,
    val verbatimProtocol: OparlFileDto? = null,
    val auxiliaryFile: List<OparlFileDto>? = null,
    val agendaItem: List<OparlAgendaItemDto>? = null,
    val license: String? = null,
    val keyword: List<String>? = null,
    val created: String? = null,
    val modified: String? = null,
    val web: String? = null,
    val deleted: Boolean? = null,
) : OparlObjectDto
