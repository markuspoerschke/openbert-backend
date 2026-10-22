package nrw.openbert.backend.parldata.domain.enrich.normalizer

import org.springframework.stereotype.Component

@Component
class StreetAddressNormalizer(
    private val nullableStringNormalizer: NullableStringNormalizer,
) {
    fun normalize(streetAddress: String?): String? {
        val normalizedString = nullableStringNormalizer.normalize(streetAddress)

        if (normalizedString == null) {
            return null
        }

        return normalizedString
            .replace(separateAbbreviation, "Straße")
            .replace(attachedAbbreviation, "straße")
    }

    private val separateAbbreviation = Regex("""(?<!\p{L})str\.(?=\s|[0-9]|$)""", RegexOption.IGNORE_CASE)
    private val attachedAbbreviation = Regex("""(?<=\p{L})str\.(?=\s|[0-9]|$)""", RegexOption.IGNORE_CASE)
}
