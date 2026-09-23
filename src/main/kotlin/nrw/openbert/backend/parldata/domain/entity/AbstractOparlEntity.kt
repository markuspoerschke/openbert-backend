package nrw.openbert.backend.parldata.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import java.time.Instant
import java.util.*

@MappedSuperclass
abstract class AbstractOparlEntity(
    @Embedded
    val resource: OparlResource,

    @Id
    @Column(nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID()
) {
    companion object {
        /**
         * A text placeholder for unknown values.
         *
         * This is used to avoid null values in fields from the Oparl schema that require a value.
         */
        const val UNKNOWN = "UNKNOWN"
    }

    @Column
    val created: Instant = Instant.now()

    @Column
    val modified: Instant = Instant.now()

    @Column
    val deleted: Boolean = false
}
