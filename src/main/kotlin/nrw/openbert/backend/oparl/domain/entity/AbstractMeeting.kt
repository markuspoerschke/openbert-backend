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

    @Column
    var name: String? = null
        protected set

    @Column
    var meetingState: String? = null
        protected set

    @Column
    var cancelled: Boolean? = null
        protected set

    @Column
    var start: Date? = null
        protected set

    @Column
    var end: Date? = null
        protected set

    @Column
    var license: String? = null
        protected set

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()
        protected set

    @Column
    var web: String? = null
        protected set

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
