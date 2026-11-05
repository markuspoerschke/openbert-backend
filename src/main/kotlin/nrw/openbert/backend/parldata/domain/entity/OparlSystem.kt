package nrw.openbert.backend.parldata.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import nrw.openbert.backend.parldata.domain.valueObject.AuthorizationHeader
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_system")
class OparlSystem(
    @Column(nullable = false, updatable = false, length = OparlUrl.MAX_LENGTH)
    val url: OparlUrl,
    @Id
    @Column(nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID(),
    // Encrypted at rest by SensitiveFieldEncryptorConverter (autoApply), discovered via
    // classpath scanning, so the domain layer does not depend on the persistence adapter.
    @Column(nullable = true, columnDefinition = "TEXT")
    var authorizationHeader: AuthorizationHeader? = null,
) {
    // Entities compare by their stable id, so OparlResource (a data class holding this
    // system) stays equal across detached/reloaded instances, e.g. in tests.
    override fun equals(other: Any?): Boolean = other is OparlSystem && other.id == id

    override fun hashCode(): Int = id.hashCode()
}
