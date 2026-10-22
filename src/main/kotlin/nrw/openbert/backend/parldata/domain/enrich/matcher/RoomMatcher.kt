package nrw.openbert.backend.parldata.domain.enrich.matcher

import org.springframework.stereotype.Component

@Component
class RoomMatcher {
    fun isRoom(room: String?): Boolean {
        if (room == null) {
            return false
        }

        return roomTerms.any { room.contains(it, ignoreCase = true) }
    }

    private val roomTerms = listOf("raum", "saal", "aula", "halle", "neubau")
}
