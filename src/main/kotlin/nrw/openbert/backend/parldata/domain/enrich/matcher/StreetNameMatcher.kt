package nrw.openbert.backend.parldata.domain.enrich.matcher

import org.springframework.stereotype.Component

@Component
class StreetNameMatcher {
    fun isStreetName(streetName: String?): Boolean {
        if (streetName == null) {
            return false
        }

        return streetName.contains("straße", true)
    }
}
