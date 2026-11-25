package nrw.openbert.backend.parldata.application.port.model

interface BodyResourceCounts {
    val persons: Long
    val organizations: Long
    val papers: Long
    val files: Long
    val meetings: Long
    val agendaItems: Long
    val locations: Long
    val consultations: Long
    val legislativeTerms: Long
    val memberships: Long
}
