package nrw.openbert.backend.resolve.domain.entity

import nrw.openbert.backend.oparl.domain.entity.AbstractOparlEntity
import nrw.openbert.backend.oparl.domain.entity.raw.RawAgendaItem
import nrw.openbert.backend.oparl.domain.entity.raw.RawBody
import nrw.openbert.backend.oparl.domain.entity.raw.RawConsultation
import nrw.openbert.backend.oparl.domain.entity.raw.RawFile
import nrw.openbert.backend.oparl.domain.entity.raw.RawLegislativeTerm
import nrw.openbert.backend.oparl.domain.entity.raw.RawLocation
import nrw.openbert.backend.oparl.domain.entity.raw.RawMeeting
import nrw.openbert.backend.oparl.domain.entity.raw.RawMembership
import nrw.openbert.backend.oparl.domain.entity.raw.RawOrganization
import nrw.openbert.backend.oparl.domain.entity.raw.RawPaper
import nrw.openbert.backend.oparl.domain.entity.raw.RawPerson
import nrw.openbert.backend.oparl.domain.entity.raw.RawSystem

enum class ResolveEntityType {
    AGENDA_ITEM,
    BODY,
    CONSULTATION,
    FILE,
    LEGISLATIVE_TERM,
    LOCATION,
    MEETING,
    MEMBERSHIP,
    ORGANIZATION,
    PAPER,
    PERSON,
    SYSTEM;

    companion object {
        fun from(entity: AbstractOparlEntity): ResolveEntityType = when (entity) {
            is RawAgendaItem -> AGENDA_ITEM
            is RawBody -> BODY
            is RawConsultation -> CONSULTATION
            is RawFile -> FILE
            is RawLegislativeTerm -> LEGISLATIVE_TERM
            is RawLocation -> LOCATION
            is RawMeeting -> MEETING
            is RawMembership -> MEMBERSHIP
            is RawOrganization -> ORGANIZATION
            is RawPaper -> PAPER
            is RawPerson -> PERSON
            is RawSystem -> SYSTEM
            else -> throw IllegalArgumentException("Unsupported raw OParl entity ${entity::class.qualifiedName}")
        }
    }
}
