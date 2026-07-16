package nrw.openbert.backend.domain.oparl.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.EmbeddedId
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import nrw.openbert.backend.infrastructure.oparl.persitence.converter.OparlUrlConverter
import kotlin.time.Clock
import kotlin.time.Instant

@MappedSuperclass
abstract class AbstractOparlEntity(
    @EmbeddedId
    val id: OparlEntityId
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
    val created: Instant = Clock.System.now()

    @Column
    val modified: Instant = Clock.System.now()

    @Column
    val deleted: Boolean = false
}
