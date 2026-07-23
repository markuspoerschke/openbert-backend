package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import nrw.openbert.backend.oparl.infrastructure.persitence.converter.OparlUrlConverter
import java.time.Instant
import java.util.*

@MappedSuperclass
abstract class AbstractOparlEntity(
    @Column(nullable = false, updatable = false)
    val systemId: UUID,

    @Convert(converter = OparlUrlConverter::class)
    @Column(nullable = false, updatable = false, length = OparlUrl.MAX_LENGTH)
    val url: OparlUrl,

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
