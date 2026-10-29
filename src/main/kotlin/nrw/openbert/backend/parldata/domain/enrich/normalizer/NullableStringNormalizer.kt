package nrw.openbert.backend.parldata.domain.enrich.normalizer

import org.springframework.stereotype.Component

@Component
class NullableStringNormalizer {
    fun normalize(input: String?): String? = input?.trim()?.takeUnless { it.isEmpty() || it == "-" }
}
