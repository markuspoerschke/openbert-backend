package nrw.openbert.backend.parldata.domain.enrich.enricher

import nrw.openbert.backend.parldata.domain.enrich.matcher.RoomMatcher
import nrw.openbert.backend.parldata.domain.enrich.matcher.StreetNameMatcher
import nrw.openbert.backend.parldata.domain.enrich.normalizer.NullableStringNormalizer
import nrw.openbert.backend.parldata.domain.enrich.normalizer.StreetAddressNormalizer
import nrw.openbert.backend.parldata.domain.entity.raw.RawLocation
import org.springframework.stereotype.Component

/**
 * Corrects unambiguous location field mistakes without changing the source data.
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
            .let { normalizeStreetName(it.getStreetAddressOverrideOrOriginalValue(location.streetAddress), it) }
            .let { normalizePostalCode(it.getPostalCodeOverrideOrOriginalValue(location.postalCode), it) }
            .let { normalizeRoom(it.getRoomOverrideOrOriginalValue(location.room), it) }
            .let { normalizeDescription(it.getDescriptionOverrideOrOriginalValue(location.description), it) }
            .let { normalizeLocality(it.getLocalityOverrideOrOriginalValue(location.locality), it) }
            // check if field values have values from other fields
            .let {
                removeRepeatedPostalCodeAndLocality(
                    it.getStreetAddressOverrideOrOriginalValue(location.streetAddress),
                    it.getPostalCodeOverrideOrOriginalValue(location.postalCode),
                    it.getLocalityOverrideOrOriginalValue(location.locality),
                    it,
                )
            }
            .let {
                normalizeStreetAddressInRoom(
                    it.getStreetAddressOverrideOrOriginalValue(location.streetAddress),
                    it.getRoomOverrideOrOriginalValue(location.room),
                    it,
                )
            }
            .let {
                removeOrganizationNameFromAddress(
                    it.getPostalCodeOverrideOrOriginalValue(location.postalCode),
                    it.getLocalityOverrideOrOriginalValue(location.locality),
                    it.getDescriptionOverrideOrOriginalValue(location.description),
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
            .let {
                rejoinSplitLocality(
                    it.getPostalCodeOverrideOrOriginalValue(location.postalCode),
                    it.getLocalityOverrideOrOriginalValue(location.locality),
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

    private fun removeRepeatedPostalCodeAndLocality(
        streetAddress: String?,
        postalCode: String?,
        locality: String?,
        result: LocationOverrides,
    ): LocationOverrides {
        if (isGermanPostalCode(postalCode) && locality != null && streetAddress == "$postalCode $locality") {
            return result.withStreetAddressOverride(null)
        }

        return result
    }

    private fun normalizeStreetAddressInRoom(
        streetAddress: String?,
        room: String?,
        result: LocationOverrides,
    ): LocationOverrides {
        if ((streetAddress == null || roomMatcher.isRoom(streetAddress)) && streetNameMatcher.isStreetName(room)) {
            return result
                .withStreetAddressOverride(streetAddressNormalizer.normalize(room))
                .withRoomOverride(streetAddress)
        }

        return result
    }

    private fun removeOrganizationNameFromAddress(
        postalCode: String?,
        locality: String?,
        description: String?,
        result: LocationOverrides,
    ): LocationOverrides {
        if (postalCode != null && !isGermanPostalCode(postalCode) && locality != null &&
            "$postalCode $locality" == description
        ) {
            return result.withPostalCodeOverride(null).withLocalityOverride(null)
        }

        return result
    }

    private fun normalizePostalCodeContainsRoom(
        postalCode: String?,
        room: String?,
        result: LocationOverrides,
    ): LocationOverrides {
        if (room == null && !isGermanPostalCode(postalCode) && roomMatcher.isRoom(postalCode)) {
            return result
                .withRoomOverride(postalCode)
                .withPostalCodeOverride(null)
        }

        return result
    }

    private fun rejoinSplitLocality(
        postalCode: String?,
        locality: String?,
        result: LocationOverrides,
    ): LocationOverrides {
        // Room terms stay in place when a room already exists; do not move them into the locality.
        if (postalCode != null && !isGermanPostalCode(postalCode) && !roomMatcher.isRoom(postalCode) &&
            locality != null
        ) {
            return result.withLocalityOverride("$postalCode $locality").withPostalCodeOverride(null)
        }

        return result
    }

    private fun isGermanPostalCode(postalCode: String?): Boolean =
        postalCode != null && germanPostalCode.matches(postalCode)

    private val germanPostalCode = Regex("[0-9]{5}")
}
