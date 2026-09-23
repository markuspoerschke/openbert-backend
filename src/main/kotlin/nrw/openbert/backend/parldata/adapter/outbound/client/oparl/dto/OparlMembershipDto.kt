package nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto

data class OparlMembershipDto(
    override val id: String? = null,
    override val type: String? = null,
    val person: String? = null,
    val organization: String? = null,
    val role: String? = null,
    val votingRight: Boolean? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val onBehalfOf: String? = null,
    val license: String? = null,
    val keyword: List<String>? = null,
    val created: String? = null,
    val modified: String? = null,
    val web: String? = null,
    val deleted: Boolean? = null,
) : OparlObjectDto
