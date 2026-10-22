package nrw.openbert.backend.parldata.domain.enrich.normalizer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class NormalizeStreetAddressTest {

    private val subjectUnderTest = StreetAddressNormalizer(NullableStringNormalizer())

    @ParameterizedTest(name = "[{index}] normalize({0}) → {1}")
    @MethodSource("normalizeProvider")
    fun testNormalize(input: String?, expected: String?) {
        assertEquals(expected, subjectUnderTest.normalize(input))
    }

    companion object {
        @JvmStatic
        fun normalizeProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(null, null),
            Arguments.of("", null),
            Arguments.of("   ", null),
            Arguments.of("Hauptstr. 12", "Hauptstraße 12"),
            Arguments.of(" Hauptstr. 12 ", "Hauptstraße 12"),
            Arguments.of(" Hauptstraße 12 ", "Hauptstraße 12"),
            Arguments.of("Max-Mustermann-Str. 12", "Max-Mustermann-Straße 12"),
            Arguments.of("Velberter Str. 12", "Velberter Straße 12"),
            Arguments.of("Nevigeser Str. 307", "Nevigeser Straße 307"),
            Arguments.of("HauptSTR. 12a", "Hauptstraße 12a"),
            Arguments.of("Velberter str. 12", "Velberter Straße 12"),
            Arguments.of("Str. 12", "Straße 12"),
            Arguments.of("Hauptstr.12", "Hauptstraße12"),
            Arguments.of("Hauptstr.example 12", "Hauptstr.example 12"),
            Arguments.of("Straße des 17. Juni 135", "Straße des 17. Juni 135"),
        )
    }
}
