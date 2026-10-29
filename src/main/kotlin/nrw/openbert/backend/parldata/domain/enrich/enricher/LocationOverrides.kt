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
    val locality: String? = null,
) {
    // street address
    fun withStreetAddressOverride(newStreetAddress: String?): LocationOverrides =
        copy(overrideStreetAddress = true, streetAddress = newStreetAddress)

    fun getStreetAddressOverrideOrOriginalValue(originalValue: String?): String? =
        if (overrideStreetAddress) streetAddress else originalValue

    // postal code
    fun withPostalCodeOverride(normalizedPostalCode: String?): LocationOverrides =
        copy(overridePostalCode = true, postalCode = normalizedPostalCode)

    fun getPostalCodeOverrideOrOriginalValue(originalValue: String?): String? =
        if (overridePostalCode) postalCode else originalValue

    // room
    fun withRoomOverride(room: String?): LocationOverrides = copy(overrideRoom = true, room = room)

    fun getRoomOverrideOrOriginalValue(originalValue: String?): String? = if (overrideRoom) room else originalValue

    // description
    fun withDescriptionOverride(description: String?): LocationOverrides =
        copy(overrideDescription = true, description = description)

    fun getDescriptionOverrideOrOriginalValue(originalValue: String?): String? =
        if (overrideDescription) description else originalValue

    // locality
    fun withLocalityOverride(locality: String?): LocationOverrides = copy(overrideLocality = true, locality = locality)

    fun getLocalityOverrideOrOriginalValue(originalValue: String?): String? =
        if (overrideLocality) locality else originalValue
}
