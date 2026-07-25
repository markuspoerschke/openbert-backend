package nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator

import nrw.openbert.backend.oparl.infrastructure.api.v1.controller.OparlBodyController
import nrw.openbert.backend.oparl.infrastructure.api.v1.controller.OparlFileController
import nrw.openbert.backend.oparl.infrastructure.api.v1.controller.OparlLegislativeTermController
import nrw.openbert.backend.oparl.infrastructure.api.v1.controller.OparlLocationController
import nrw.openbert.backend.oparl.infrastructure.api.v1.controller.OparlMeetingController
import nrw.openbert.backend.oparl.infrastructure.api.v1.controller.OparlMembershipController
import nrw.openbert.backend.oparl.infrastructure.api.v1.controller.OparlOrganizationController
import nrw.openbert.backend.oparl.infrastructure.api.v1.controller.OparlPaperController
import nrw.openbert.backend.oparl.infrastructure.api.v1.controller.OparlPersonController
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.util.UUID

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
        return "TODO"
    }

    fun consultationList(bodyId: UUID): String {
        return "TODO"
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
