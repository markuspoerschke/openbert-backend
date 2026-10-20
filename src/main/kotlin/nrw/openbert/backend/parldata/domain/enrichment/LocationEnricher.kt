package nrw.openbert.backend.parldata.domain.enrichment

import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedLocation

/** Deterministic corrections for resolved locations; imported source values remain untouched. */
object LocationEnricher {
    private val whitespace = Regex("[\\s\\p{Z}]+")
    private val germanPostalCode = Regex("[0-9]{5}")
    private val roomTerm = Regex("raum|saal|aula|halle|neubau", RegexOption.IGNORE_CASE)
    private val streetAddress = Regex(
        "[\\p{L} .'-]*(?:straße|strasse|str\\.|weg|platz|allee|gasse|ring|damm|ufer|chaussee|steig|markt|kamp)" +
            " +[0-9]+(?: *[a-z])?(?: *[-/] *[0-9]+(?: *[a-z])?)?",
        RegexOption.IGNORE_CASE,
    )
    private val streetAbbreviation = Regex("str\\.(?= |$)", RegexOption.IGNORE_CASE)

    fun enrich(location: ResolvedLocation) = with(location) {
        description = description.normalized()
        streetAddress = streetAddress.normalized()
        postalCode = postalCode.normalized()
        locality = locality.normalized()
        subLocality = subLocality.normalized()
        room = room.normalized()

        correctPostalCode(this)

        if (postalCode?.matches(germanPostalCode) == true && locality != null &&
            streetAddress == "$postalCode $locality"
        ) {
            streetAddress = null
        }

        if (isRoom(streetAddress) && isStreetAddress(room)) {
            val originalRoom = room
            room = streetAddress
            streetAddress = originalRoom
        } else if (streetAddress == null && isStreetAddress(room)) {
            streetAddress = room
            room = null
        }

        streetAddress = streetAddress?.let { address ->
            streetAbbreviation.replace(address) { match ->
                if (match.range.first > 0 && address[match.range.first - 1].isLetter()) "straße" else "Straße"
            }
        }
    }

    private fun correctPostalCode(location: ResolvedLocation) = with(location) {
        val postal = postalCode ?: return@with
        if (postal.matches(germanPostalCode)) return@with

        // Specific patterns must precede the fallback that rejoins split locality text.
        when {
            locality != null && "$postal $locality" == description -> {
                postalCode = null
                locality = null
            }
            isRoom(postal) -> if (room == null) {
                room = postal
                postalCode = null
            }
            locality != null -> {
                locality = "$postal $locality"
                postalCode = null
            }
        }
    }

    private fun String?.normalized(): String? = this?.replace(whitespace, " ")?.trim()?.takeIf { it.isNotEmpty() }

    private fun isStreetAddress(value: String?): Boolean = value?.matches(streetAddress) == true

    private fun isRoom(value: String?): Boolean =
        value != null && roomTerm.containsMatchIn(value) && !isStreetAddress(value)
}
