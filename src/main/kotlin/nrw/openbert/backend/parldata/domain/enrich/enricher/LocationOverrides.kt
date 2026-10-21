package nrw.openbert.backend.parldata.domain.enrich.enricher

data class LocationOverrides(
    val overrideStreetAddress: Boolean = false,
    val streetAddress: String? = null,
    val overridePostalCode: Boolean = false,
    val postalCode: String? = null,
    val overrideRoom: Boolean = false,
    val room: String? = null,
    val overrideDescription: Boolean = false,
    val description: String? = null,
    val overrideLocality: Boolean = false,
    val locality: String? = null
) {
    // street address
    fun withStreetAddressOverride(newStreetAddress: String?): LocationOverrides {
        return copy(overrideStreetAddress = true, streetAddress = newStreetAddress)
    }

    fun getStreetAddressOverrideOrOriginalValue(originalValue: String?): String?
    {
        return if (overrideStreetAddress) streetAddress else originalValue
    }

    // postal code
    fun withPostalCodeOverride(normalizedPostalCode: String?): LocationOverrides {
        return copy(overridePostalCode = true, postalCode = normalizedPostalCode)
    }

    fun getPostalCodeOverrideOrOriginalValue(originalValue: String?): String?
    {
        return if (overridePostalCode) postalCode else originalValue
    }

    // room
    fun withRoomOverride(room: String?): LocationOverrides {
        return copy(overrideRoom = true, room = room)
    }

    fun getRoomOverrideOrOriginalValue(originalValue: String?): String?
    {
        return if (overrideRoom) room else originalValue
    }

    // description
    fun withDescriptionOverride(description: String?): LocationOverrides {
        return copy(overrideDescription = true, description = description)
    }

    fun getDescriptionOverrideOrOriginalValue(originalValue: String?): String?
    {
        return if (overrideDescription) description else originalValue
    }

    // locality
    fun withLocalityOverride(locality: String?): LocationOverrides {
        return copy(overrideLocality = true, locality = locality)
    }

    fun getLocalityOverrideOrOriginalValue(originalValue: String?): String?
    {
        return if (overrideLocality) locality else originalValue
    }
}
