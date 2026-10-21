package nrw.openbert.backend.parldata.domain.enrich.normalizer

import org.springframework.stereotype.Component

@Component
class NullableStringNormalizer {
    fun normalize(input: String?): String? {
        if (input == null) {
            return null
        }

        val normalizedString = input.trim()

        if (normalizedString == "" || normalizedString == "-") {
            return null
        }

        return normalizedString
    }
}
