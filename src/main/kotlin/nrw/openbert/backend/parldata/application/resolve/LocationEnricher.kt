package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.domain.entity.AbstractLocation
import org.springframework.stereotype.Service

@Service
class LocationEnricher {
    private val germanPostalCode = Regex("""^\d{5}$""")
    private val roomTerms = listOf("raum", "saal", "aula", "halle", "neubau")
    private val streetAbbreviation = Regex("""(?i)\bStr\.\s*""")
    private val multipleSpaces = Regex("""\s+""")

    fun enrich(location: AbstractLocation) {
        normalizeWhitespace(location)

        if (streetAddressRepeatsPostalCodeAndLocality(location)) {
            location.streetAddress = null
            if (isStreetAddress(location.room)) {
                location.streetAddress = location.room
                location.room = null
            }
        }

        swapStreetAddressAndRoom(location)
        clearOrganizationNameInAddressFields(location)
        moveRoomNameFromPostalCode(location)
        rejoinSplitLocality(location)
        expandStreetAbbreviation(location)
    }

    private fun normalizeWhitespace(location: AbstractLocation) {
        location.description = location.description?.normalize()
        location.streetAddress = location.streetAddress?.normalize()
        location.postalCode = location.postalCode?.normalize()
        location.locality = location.locality?.normalize()
        location.subLocality = location.subLocality?.normalize()
        location.room = location.room?.normalize()
    }

    private fun streetAddressRepeatsPostalCodeAndLocality(location: AbstractLocation): Boolean {
        val postalCode = location.postalCode ?: return false
        val locality = location.locality ?: return false
        val streetAddress = location.streetAddress ?: return false
        return streetAddress.equals("$postalCode $locality", ignoreCase = true)
    }

    private fun swapStreetAddressAndRoom(location: AbstractLocation) {
        val streetAddress = location.streetAddress ?: return
        val room = location.room ?: return
        if (isRoomName(streetAddress) && isStreetAddress(room)) {
            location.streetAddress = room
            location.room = streetAddress
        }
    }

    private fun clearOrganizationNameInAddressFields(location: AbstractLocation) {
        val postalCode = location.postalCode ?: return
        val locality = location.locality ?: return
        val description = location.description ?: return
        if (isValidPostalCode(postalCode)) return
        if (!"$postalCode $locality".equals(description, ignoreCase = true)) return
        location.postalCode = null
        location.locality = null
    }

    private fun moveRoomNameFromPostalCode(location: AbstractLocation) {
        val postalCode = location.postalCode ?: return
        if (!location.room.isNullOrBlank()) return
        if (isValidPostalCode(postalCode)) return
        if (!isRoomName(postalCode)) return
        location.room = postalCode
        location.postalCode = null
    }

    private fun rejoinSplitLocality(location: AbstractLocation) {
        val postalCode = location.postalCode ?: return
        if (isValidPostalCode(postalCode)) return
        if (isRoomName(postalCode)) return
        location.locality = listOfNotNull(postalCode, location.locality).joinToString(" ")
        location.postalCode = null
    }

    private fun expandStreetAbbreviation(location: AbstractLocation) {
        location.streetAddress = location.streetAddress?.replace(streetAbbreviation, "Straße ")?.normalize()
    }

    private fun isValidPostalCode(value: String): Boolean = germanPostalCode.matches(value)

    private fun isRoomName(value: String?): Boolean {
        if (value.isNullOrBlank()) return false
        val lowerCase = value.lowercase()
        return roomTerms.any { lowerCase.contains(it) }
    }

    private fun isStreetAddress(value: String?): Boolean {
        if (value.isNullOrBlank()) return false
        if (isRoomName(value)) return false
        return value.any { it.isDigit() }
    }

    private fun String.normalize(): String = replace(multipleSpaces, " ").trim()
}
