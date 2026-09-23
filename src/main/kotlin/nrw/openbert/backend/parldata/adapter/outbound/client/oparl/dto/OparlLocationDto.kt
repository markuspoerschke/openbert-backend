package nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto

data class OparlLocationDto(
    override val id: String? = null,
    override val type: String? = null,
    val description: String? = null,
    val geojson: Map<String, Any?>? = null,
    val streetAddress: String? = null,
    val room: String? = null,
    val postalCode: String? = null,
    val subLocality: String? = null,
    val locality: String? = null,
    val bodies: List<String>? = null,
    val organizations: List<String>? = null,
    val persons: List<String>? = null,
    val meetings: List<String>? = null,
    val papers: List<String>? = null,
    val license: String? = null,
    val keyword: List<String>? = null,
    val created: String? = null,
    val modified: String? = null,
    val web: String? = null,
    val deleted: Boolean? = null,
) : OparlObjectDto
