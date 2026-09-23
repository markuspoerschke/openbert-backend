package nrw.openbert.backend.parldata.domain.enums

import nrw.openbert.backend.parldata.domain.entity.AbstractOparlEntity
import nrw.openbert.backend.parldata.domain.entity.raw.RawAgendaItem
import nrw.openbert.backend.parldata.domain.entity.raw.RawBody
import nrw.openbert.backend.parldata.domain.entity.raw.RawConsultation
import nrw.openbert.backend.parldata.domain.entity.raw.RawFile
import nrw.openbert.backend.parldata.domain.entity.raw.RawLegislativeTerm
import nrw.openbert.backend.parldata.domain.entity.raw.RawLocation
import nrw.openbert.backend.parldata.domain.entity.raw.RawMeeting
import nrw.openbert.backend.parldata.domain.entity.raw.RawMembership
import nrw.openbert.backend.parldata.domain.entity.raw.RawOrganization
import nrw.openbert.backend.parldata.domain.entity.raw.RawPaper
import nrw.openbert.backend.parldata.domain.entity.raw.RawPerson
import nrw.openbert.backend.parldata.domain.entity.raw.RawSystem

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
