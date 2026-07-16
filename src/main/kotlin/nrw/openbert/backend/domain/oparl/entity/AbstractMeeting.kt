package nrw.openbert.backend.domain.oparl.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@MappedSuperclass
abstract class AbstractMeeting(id: OparlEntityId) : AbstractOparlEntity(id) {
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var keyword: Array<String> = emptyArray()

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
