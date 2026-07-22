package nrw.openbert.backend.infrastructure.oparl.api

import nrw.openbert.backend.infrastructure.oparl.api.controller.v1.OparlBodyController
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.util.UUID

@Component
class OparlUrlGenerator {
    fun bodiesUrl(): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlBodyController::class.java, "list")
            .build()
            .toString()
    }

    fun bodyUrl(bodyId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(OparlBodyController::class.java, "get", bodyId)
            .build()
            .toString()
    }

    fun organizationsUrl(bodyId: UUID): String {
        return "TODO"
    }

    fun personsUrl(bodyId: UUID): String {
        return "TODO"
    }

    fun meetingsUrl(bodyId: UUID): String {
        return "TODO"
    }

    fun papersUrl(bodyId: UUID): String {
        return "TODO"
    }

    fun agendaItemsUrl(bodyId: UUID): String {
        return "TODO"
    }

    fun consultationsUrl(bodyId: UUID): String {
        return "TODO"
    }

    fun filesUrl(bodyId: UUID): String {
        return "TODO"
    }

    fun legislativeTermsUrl(bodyId: UUID): String {
        return "TODO"
    }

    fun membershipsUrl(bodyId: UUID): String {
        return "TODO"
    }

    fun locationsUrl(body: UUID): String
    {
        return "TODO"
    }
}
