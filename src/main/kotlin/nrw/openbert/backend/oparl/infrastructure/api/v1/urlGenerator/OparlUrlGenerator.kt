package nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator

import nrw.openbert.backend.oparl.infrastructure.api.v1.controller.OparlBodyController
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
        return "TODO"
    }

    fun personList(bodyId: UUID): String {
        return "TODO"
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