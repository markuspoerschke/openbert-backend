package nrw.openbert.backend.parldata.domain.enrich.enricher

import nrw.openbert.backend.parldata.domain.enrich.matcher.RoomMatcher
import nrw.openbert.backend.parldata.domain.enrich.matcher.StreetNameMatcher
import nrw.openbert.backend.parldata.domain.enrich.normalizer.NullableStringNormalizer
import nrw.openbert.backend.parldata.domain.enrich.normalizer.StreetAddressNormalizer
import nrw.openbert.backend.parldata.domain.entity.raw.RawLocation
import org.springframework.stereotype.Component

/**
 * This normalizer normalizes the field values in a basic manner like removing leading and trailing whitespaces.
 */
@Component
class SimpleLocationEnricher(
    private val nullableStringNormalizer: NullableStringNormalizer,
    private val streetAddressNormalizer: StreetAddressNormalizer,
    private val roomMatcher: RoomMatcher,
    private val streetNameMatcher: StreetNameMatcher,
) {
    fun enrich(location: RawLocation, result: LocationOverrides): LocationOverrides {
        return result
            // normalize field values
            .let { normalizeStreetName(location.streetAddress, it) }
            .let { normalizePostalCode(location.postalCode, it) }
            .let { normalizeRoom(location.room, it) }
            .let { normalizeDescription(location.description, it) }
            .let { normalizeLocality(location.locality, it) }
            // check if field values have values from other fields
            .let {
                normalizedSwappedStreetNameAndRoom(
                    it.getStreetAddressOverrideOrOriginalValue(location.streetAddress),
                    it.getRoomOverrideOrOriginalValue(location.room),
                    it,
                )
            }
            .let {
                normalizePostalCodeContainsRoom(
                    it.getPostalCodeOverrideOrOriginalValue(location.postalCode),
                    it.getRoomOverrideOrOriginalValue(location.room),
                    it,
                )
            }
    }

    private fun normalizeStreetName(streetName: String?, result: LocationOverrides): LocationOverrides {
        val normalizedStreetName = streetAddressNormalizer.normalize(streetName)

        if (normalizedStreetName == streetName) {
            return result
        }

        return result.withStreetAddressOverride(normalizedStreetName)
    }

    private fun normalizePostalCode(postalCode: String?, result: LocationOverrides): LocationOverrides {
        val normalizedPostalCode = nullableStringNormalizer.normalize(postalCode)

        if (normalizedPostalCode == postalCode) {
            return result
        }

        return result.withPostalCodeOverride(normalizedPostalCode)
    }

    private fun normalizeRoom(room: String?, result: LocationOverrides): LocationOverrides {
        val normalizedRoom = nullableStringNormalizer.normalize(room)

        if (normalizedRoom == room) {
            return result
        }

        return result.withRoomOverride(normalizedRoom)
    }

    private fun normalizeDescription(description: String?, result: LocationOverrides): LocationOverrides {
        val normalizedDescription = nullableStringNormalizer.normalize(description)

        if (normalizedDescription == description) {
            return result
        }

        return result.withDescriptionOverride(normalizedDescription)
    }

    private fun normalizeLocality(locality: String?, result: LocationOverrides): LocationOverrides {
        val normalizedLocality = nullableStringNormalizer.normalize(locality)

        if (normalizedLocality == locality) {
            return result
        }

        return result.withLocalityOverride(normalizedLocality)
    }

    private fun normalizedSwappedStreetNameAndRoom(
        streetAddress: String?,
        room: String?,
        result: LocationOverrides
    ): LocationOverrides {
        if (roomMatcher.isRoom(streetAddress) && streetNameMatcher.isStreetName(room)) {
            // the field room and streetName are swapped
            return result
                .withStreetAddressOverride(room)
                .withRoomOverride(streetAddress)
        }

        return result
    }

    private fun normalizePostalCodeContainsRoom(
        postalCode: String?,
        room: String?,
        result: LocationOverrides
    ): LocationOverrides {
        if (room == null && roomMatcher.isRoom(postalCode)) {
            return result
                .withRoomOverride(postalCode)
                .withPostalCodeOverride(null)
        }

        return result
    }
}
