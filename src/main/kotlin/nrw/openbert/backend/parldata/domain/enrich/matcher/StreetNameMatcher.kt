package nrw.openbert.backend.parldata.domain.enrich.matcher

import org.springframework.stereotype.Component

@Component
class StreetNameMatcher {
    fun isStreetName(streetName: String?): Boolean {
        return streetName != null && streetAddress.matches(streetName)
    }

    // Require a known street suffix and a house number before moving address fields.
    private val streetAddress = Regex(
        """(?=\p{L})[\p{L}\p{N} .'-]*(?:straße|str\.|weg|platz|gasse|allee|ring|damm|ufer|chaussee|steig|markt)\s*[0-9]+(?:\s?[a-z])?(?:\s*[-/]\s*[0-9]+(?:\s?[a-z])?)?""",
        RegexOption.IGNORE_CASE,
    )
}
