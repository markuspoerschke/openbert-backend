package nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator

import nrw.openbert.backend.parldata.adapter.inbound.api.controller.OparlAgendaItemController
import nrw.openbert.backend.parldata.adapter.inbound.api.controller.OparlBodyController
import nrw.openbert.backend.parldata.adapter.inbound.api.controller.OparlConsultationController
import nrw.openbert.backend.parldata.adapter.inbound.api.controller.OparlFileController
import nrw.openbert.backend.parldata.adapter.inbound.api.controller.OparlLegislativeTermController
import nrw.openbert.backend.parldata.adapter.inbound.api.controller.OparlLocationController
import nrw.openbert.backend.parldata.adapter.inbound.api.controller.OparlMeetingController
import nrw.openbert.backend.parldata.adapter.inbound.api.controller.OparlMembershipController
import nrw.openbert.backend.parldata.adapter.inbound.api.controller.OparlOrganizationController
import nrw.openbert.backend.parldata.adapter.inbound.api.controller.OparlPaperController
import nrw.openbert.backend.parldata.adapter.inbound.api.controller.OparlPersonController
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.util.UUID
import kotlin.jvm.java

@Component
class OparlUrlGenerator {
    fun bodyList(): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlBodyController::class.java, "list")
            .build()
            .toString()
    }

    fun body(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlBodyController::class.java, "get", bodyId)
            .build()
            .toString()
    }

    fun organizationList(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlOrganizationController::class.java, "list", bodyId)
            .build()
            .toString()
    }

    fun organization(bodyId: UUID, organizationId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlOrganizationController::class.java, "get", bodyId, organizationId)
            .build()
            .toString()
    }

    fun organizationMeetingList(bodyId: UUID, organizationId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(
                OparlOrganizationController::class.java,
                "listMeetings",
                bodyId,
                organizationId,
            )
            .build()
            .toString()
    }

    fun organizationConsultationList(bodyId: UUID, organizationId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(
                OparlOrganizationController::class.java,
                "listConsultations",
                bodyId,
                organizationId,
            )
            .build()
            .toString()
    }

    fun personList(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlPersonController::class.java, "list", bodyId)
            .build()
            .toString()
    }

    fun person(bodyId: UUID, personId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlPersonController::class.java, "get", bodyId, personId)
            .build()
            .toString()
    }

    fun meetingList(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlMeetingController::class.java, "list", bodyId)
            .build()
            .toString()
    }

    fun meeting(bodyId: UUID, meetingId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlMeetingController::class.java, "get", bodyId, meetingId)
            .build()
            .toString()
    }

    fun paperList(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlPaperController::class.java, "list", bodyId)
            .build()
            .toString()
    }

    fun paper(bodyId: UUID, paperId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlPaperController::class.java, "get", bodyId, paperId)
            .build()
            .toString()
    }

    fun agendaItemList(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlAgendaItemController::class.java, "list", bodyId)
            .build()
            .toString()
    }

    fun agendaItem(bodyId: UUID, agendaItemId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlAgendaItemController::class.java, "get", bodyId, agendaItemId)
            .build()
            .toString()
    }

    fun consultationList(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlConsultationController::class.java, "list", bodyId)
            .build()
            .toString()
    }

    fun consultation(bodyId: UUID, consultationId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(
                OparlConsultationController::class.java,
                "get",
                bodyId,
                consultationId,
            )
            .build()
            .toString()
    }

    fun fileList(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlFileController::class.java, "list", bodyId)
            .build()
            .toString()
    }

    fun file(bodyId: UUID, fileId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlFileController::class.java, "get", bodyId, fileId)
            .build()
            .toString()
    }

    fun fileAccess(bodyId: UUID, fileId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlFileController::class.java, "access", bodyId, fileId)
            .build()
            .toString()
    }

    fun fileDownload(bodyId: UUID, fileId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlFileController::class.java, "download", bodyId, fileId)
            .build()
            .toString()
    }

    fun legislativeTermList(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlLegislativeTermController::class.java, "list", bodyId)
            .build()
            .toString()
    }

    fun legislativeTerm(bodyId: UUID, legislativeTermId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(
                OparlLegislativeTermController::class.java,
                "get",
                bodyId,
                legislativeTermId,
            )
            .build()
            .toString()
    }

    fun membershipList(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlMembershipController::class.java, "list", bodyId)
            .build()
            .toString()
    }

    fun membership(bodyId: UUID, membershipId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlMembershipController::class.java, "get", bodyId, membershipId)
            .build()
            .toString()
    }

    fun locationList(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlLocationController::class.java, "list", bodyId)
            .build()
            .toString()
    }

    fun location(bodyId: UUID, locationId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlLocationController::class.java, "get", bodyId, locationId)
            .build()
            .toString()
    }
}
