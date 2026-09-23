package nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto

data class OparlSystemDto(
    override val id: String? = null,
    override val type: String? = null,
    val oparlVersion: String? = null,
    val otherOparlVersions: List<String>? = null,
    val license: String? = null,
    val body: String? = null,
    val name: String? = null,
    val contactEmail: String? = null,
    val contactName: String? = null,
    val website: String? = null,
    val vendor: String? = null,
    val product: String? = null,
    val created: String? = null,
    val modified: String? = null,
    val web: String? = null,
    val deleted: Boolean? = null,
) : OparlObjectDto
