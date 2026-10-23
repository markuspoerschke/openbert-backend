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
    val organization = OparlEntityUrlGenerator(OparlOrganizationController::class.java)
    val person = OparlEntityUrlGenerator(OparlPersonController::class.java)
    val meeting = OparlEntityUrlGenerator(OparlMeetingController::class.java)
    val paper = OparlEntityUrlGenerator(OparlPaperController::class.java)
    val agendaItem = OparlEntityUrlGenerator(OparlAgendaItemController::class.java)
    val consultation = OparlEntityUrlGenerator(OparlConsultationController::class.java)
    val file = OparlEntityUrlGenerator(OparlFileController::class.java)
    val legislativeTerm = OparlEntityUrlGenerator(OparlLegislativeTermController::class.java)
    val membership = OparlEntityUrlGenerator(OparlMembershipController::class.java)
    val location = OparlEntityUrlGenerator(OparlLocationController::class.java)

    fun bodyList(page: Int = 1): String {
        return paginatedList(OparlBodyController::class.java, "list", page)
    }

    fun body(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlBodyController::class.java, "get", bodyId)
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

    fun fileAccess(bodyId: UUID, fileId: UUID): String {
        // The servlet response argument is ignored when contributing URI components.
        return MvcUriComponentsBuilder
            .fromMethodName(OparlFileController::class.java, "access", bodyId, fileId, Unit)
            .build()
            .toString()
    }

    fun fileDownload(bodyId: UUID, fileId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlFileController::class.java, "download", bodyId, fileId, Unit)
            .build()
            .toString()
    }
}
