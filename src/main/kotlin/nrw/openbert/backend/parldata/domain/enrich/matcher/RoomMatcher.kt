package nrw.openbert.backend.parldata.domain.enrich.matcher

import org.springframework.stereotype.Component

@Component
class RoomMatcher {
    fun isRoom(room: String?): Boolean {
        if (room == null) {
            return false
        }

        return room.contains("raum", true) ||
                room.contains("aula", true) ||
                room.contains("saal", true)
    }
}
