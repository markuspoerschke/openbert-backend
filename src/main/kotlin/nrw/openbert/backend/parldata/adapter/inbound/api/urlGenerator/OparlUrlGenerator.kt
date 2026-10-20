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
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.util.UUID
import kotlin.jvm.java

@Component
class OparlUrlGenerator {
    fun bodyList(page: Int = 1): String {
        return paginatedList(OparlBodyController::class.java, "list", page)
    }

    fun body(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlBodyController::class.java, "get", bodyId)
            .build()
            .toString()
    }

    fun organizationList(bodyId: UUID, page: Int = 1): String {
        return paginatedList(OparlOrganizationController::class.java, "list", page, bodyId)
    }

    fun organization(bodyId: UUID, organizationId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlOrganizationController::class.java, "get", bodyId, organizationId)
            .build()
            .toString()
    }

    fun organizationMeetingList(bodyId: UUID, organizationId: UUID, page: Int = 1): String {
        return paginatedList(
            OparlOrganizationController::class.java,
            "listMeetings",
            page,
            bodyId,
            organizationId,
        )
    }

    fun organizationConsultationList(bodyId: UUID, organizationId: UUID, page: Int = 1): String {
        return paginatedList(
            OparlOrganizationController::class.java,
            "listConsultations",
            page,
            bodyId,
            organizationId,
        )
    }

    fun personList(bodyId: UUID, page: Int = 1): String {
        return paginatedList(OparlPersonController::class.java, "list", page, bodyId)
    }

    fun person(bodyId: UUID, personId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlPersonController::class.java, "get", bodyId, personId)
            .build()
            .toString()
    }

    fun meetingList(bodyId: UUID, page: Int = 1): String {
        return paginatedList(OparlMeetingController::class.java, "list", page, bodyId)
    }

    fun meeting(bodyId: UUID, meetingId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlMeetingController::class.java, "get", bodyId, meetingId)
            .build()
            .toString()
    }

    fun paperList(bodyId: UUID, page: Int = 1): String {
        return paginatedList(OparlPaperController::class.java, "list", page, bodyId)
    }

    fun paper(bodyId: UUID, paperId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlPaperController::class.java, "get", bodyId, paperId)
            .build()
            .toString()
    }

    fun agendaItemList(bodyId: UUID, page: Int = 1): String {
        return paginatedList(OparlAgendaItemController::class.java, "list", page, bodyId)
    }

    fun agendaItem(bodyId: UUID, agendaItemId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlAgendaItemController::class.java, "get", bodyId, agendaItemId)
            .build()
            .toString()
    }

    fun consultationList(bodyId: UUID, page: Int = 1): String {
        return paginatedList(OparlConsultationController::class.java, "list", page, bodyId)
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

    fun fileList(bodyId: UUID, page: Int = 1): String {
        return paginatedList(OparlFileController::class.java, "list", page, bodyId)
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

    fun legislativeTermList(bodyId: UUID, page: Int = 1): String {
        return paginatedList(OparlLegislativeTermController::class.java, "list", page, bodyId)
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

    fun membershipList(bodyId: UUID, page: Int = 1): String {
        return paginatedList(OparlMembershipController::class.java, "list", page, bodyId)
    }

    fun membership(bodyId: UUID, membershipId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlMembershipController::class.java, "get", bodyId, membershipId)
            .build()
            .toString()
    }

    fun locationList(bodyId: UUID, page: Int = 1): String {
        return paginatedList(OparlLocationController::class.java, "list", page, bodyId)
    }

    fun location(bodyId: UUID, locationId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlLocationController::class.java, "get", bodyId, locationId)
            .build()
            .toString()
    }

    /**
     * [MvcUriComponentsBuilder] only renders `@PathVariable` and `@RequestParam` arguments. The pagination DTO is
     * passed for method matching only, its `page` value has to be appended as query parameter explicitly.
     */
    private fun paginatedList(
        controller: Class<*>,
        methodName: String,
        page: Int,
        vararg pathVariables: Any,
    ): String {
        return MvcUriComponentsBuilder
            .fromMethodName(controller, methodName, *pathVariables, OparlPaginationRequestDto(page))
            .queryParam("page", page)
            .build()
            .toString()
    }
}
