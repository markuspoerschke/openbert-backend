package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.*

@MappedSuperclass
abstract class AbstractMeeting(systemId: UUID, url: OparlUrl) : AbstractOparlEntity(systemId, url) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Meeting"
    }

    @Column
    private var name: String? = null

    @Column
    private var meetingState: String? = null

    @Column
    private var cancelled: Boolean? = null

    @Column
    private var start: Date? = null

    @Column
    private var end: Date? = null

    @Column
    private var license: String? = null

    @Column(columnDefinition = "text[]")
    private var keyword: MutableList<String> = mutableListOf()

    @Column
    private var web: String? = null

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
