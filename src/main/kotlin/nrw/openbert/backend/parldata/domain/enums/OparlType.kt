package nrw.openbert.backend.parldata.domain.enums

enum class OparlType(val url: String) {
    AGENDA_ITEM("https://schema.oparl.org/1.1/AgendaItem"),
    BODY("https://schema.oparl.org/1.1/Body"),
    CONSULTATION("https://schema.oparl.org/1.1/Consultation"),
    FILE("https://schema.oparl.org/1.1/File"),
    LEGISLATIVE_TERM("https://schema.oparl.org/1.1/LegislativeTerm"),
    LOCATION("https://schema.oparl.org/1.1/Location"),
    MEETING("https://schema.oparl.org/1.1/Meeting"),
    MEMBERSHIP("https://schema.oparl.org/1.1/Membership"),
    ORGANIZATION("https://schema.oparl.org/1.1/Organization"),
    PAPER("https://schema.oparl.org/1.1/Paper"),
    PERSON("https://schema.oparl.org/1.1/Person"),
    SYSTEM("https://schema.oparl.org/1.1/System"),
}
