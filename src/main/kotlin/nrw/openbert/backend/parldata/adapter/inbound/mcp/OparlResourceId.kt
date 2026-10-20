package nrw.openbert.backend.parldata.adapter.inbound.mcp

import nrw.openbert.backend.parldata.domain.enums.OparlType
import java.util.UUID

data class OparlResourceId(
    val type: OparlType,
    val id: UUID,
) {
    companion object {
        fun fromUrl(url: String): OparlResourceId? {
            val trimmed = url.trim()
            BODY_URL.find(trimmed)?.let { match ->
                return OparlResourceId(OparlType.BODY, match.groupValues[1].toUuidOrNull() ?: return null)
            }

            val match = RESOURCE_URL.find(trimmed) ?: return null
            val entityName = match.groupValues[2].lowercase().let { ENTITY_NAMES[it] } ?: return null

            return OparlResourceId(entityName, match.groupValues[3].toUuidOrNull() ?: return null)
        }

        private fun String.toUuidOrNull(): UUID? = try {
            UUID.fromString(this)
        } catch (_: IllegalArgumentException) {
            null
        }

        private const val UUID_PATTERN =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
        private val BODY_URL = Regex(".*/oparl/v1\\.1/body/($UUID_PATTERN)$")
        private val RESOURCE_URL = Regex(".*/oparl/v1\\.1/body/$UUID_PATTERN/([a-z-]+)/($UUID_PATTERN)(?:/.*)?$")

        private val ENTITY_NAMES = mapOf(
            "agendaitem" to OparlType.AGENDA_ITEM,
            "consultation" to OparlType.CONSULTATION,
            "file" to OparlType.FILE,
            "legislativeterm" to OparlType.LEGISLATIVE_TERM,
            "location" to OparlType.LOCATION,
            "meeting" to OparlType.MEETING,
            "membership" to OparlType.MEMBERSHIP,
            "organization" to OparlType.ORGANIZATION,
            "paper" to OparlType.PAPER,
            "person" to OparlType.PERSON,
        )
    }
}
