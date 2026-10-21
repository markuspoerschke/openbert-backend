package nrw.openbert.backend.parldata.domain.enrich.normalizer

import org.springframework.stereotype.Component

@Component
class StreetAddressNormalizer(
    private val nullableStringNormalizer: NullableStringNormalizer,
) {
    fun normalize(streetAddress: String?): String? {
        val normalizedString = nullableStringNormalizer.normalize(streetAddress)

        if (normalizedString === null) {
            return null
        }

        return normalizedString
            .replace(" Str.", " Straße")
            .replace("str.", "straße")
            .replace("-Str.", "-Straße")
    }
}
