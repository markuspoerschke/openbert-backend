package nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator

import nrw.openbert.backend.oparl.infrastructure.api.v1.controller.OparlBodyController
import nrw.openbert.backend.oparl.infrastructure.api.v1.controller.OparlOrganizationController
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
        return "TODO"
    }

    fun paperList(bodyId: UUID): String {
        return "TODO"
    }

    fun agendaItemList(bodyId: UUID): String {
        return "TODO"
    }

    fun consultationList(bodyId: UUID): String {
        return "TODO"
    }

    fun fileList(bodyId: UUID): String {
        return "TODO"
    }

    fun legislativeTermList(bodyId: UUID): String {
        return "TODO"
    }

    fun membershipList(bodyId: UUID): String {
        return "TODO"
    }

    fun locationList(body: UUID): String
    {
        return "TODO"
    }
}
