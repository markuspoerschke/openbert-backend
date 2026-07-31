package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import java.util.*

@MappedSuperclass
abstract class AbstractMeeting(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Meeting"
    }

    @Column(length = 500)
    var name: String? = null

    @Column
    var meetingState: String? = null

    @Column
    var cancelled: Boolean? = null

    @Column
    var start: Date? = null

    @Column(name = "\"end\"")
    var end: Date? = null

    @Column
    var license: String? = null

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()

    @Column
    var web: String? = null

    fun applyValuesFrom(other: AbstractMeeting) {
        name = other.name
        meetingState = other.meetingState
        cancelled = other.cancelled
        start = other.start
        end = other.end
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
