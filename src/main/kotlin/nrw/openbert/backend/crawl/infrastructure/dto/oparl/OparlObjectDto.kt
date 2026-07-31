package nrw.openbert.backend.crawl.infrastructure.dto.oparl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true,
)
@JsonSubTypes(
    JsonSubTypes.Type(OparlAgendaItemDto::class, name = "https://schema.oparl.org/1.0/AgendaItem"),
    JsonSubTypes.Type(OparlBodyDto::class, name = "https://schema.oparl.org/1.0/Body"),
    JsonSubTypes.Type(OparlConsultationDto::class, name = "https://schema.oparl.org/1.0/Consultation"),
    JsonSubTypes.Type(OparlFileDto::class, name = "https://schema.oparl.org/1.0/File"),
    JsonSubTypes.Type(OparlLegislativeTermDto::class, name = "https://schema.oparl.org/1.0/LegislativeTerm"),
    JsonSubTypes.Type(OparlLocationDto::class, name = "https://schema.oparl.org/1.0/Location"),
    JsonSubTypes.Type(OparlMeetingDto::class, name = "https://schema.oparl.org/1.0/Meeting"),
    JsonSubTypes.Type(OparlMembershipDto::class, name = "https://schema.oparl.org/1.0/Membership"),
    JsonSubTypes.Type(OparlOrganizationDto::class, name = "https://schema.oparl.org/1.0/Organization"),
    JsonSubTypes.Type(OparlPaperDto::class, name = "https://schema.oparl.org/1.0/Paper"),
    JsonSubTypes.Type(OparlPersonDto::class, name = "https://schema.oparl.org/1.0/Person"),
    JsonSubTypes.Type(OparlSystemDto::class, name = "https://schema.oparl.org/1.0/System"),
    JsonSubTypes.Type(OparlAgendaItemDto::class, name = "https://schema.oparl.org/1.1/AgendaItem"),
    JsonSubTypes.Type(OparlBodyDto::class, name = "https://schema.oparl.org/1.1/Body"),
    JsonSubTypes.Type(OparlConsultationDto::class, name = "https://schema.oparl.org/1.1/Consultation"),
    JsonSubTypes.Type(OparlFileDto::class, name = "https://schema.oparl.org/1.1/File"),
    JsonSubTypes.Type(OparlLegislativeTermDto::class, name = "https://schema.oparl.org/1.1/LegislativeTerm"),
    JsonSubTypes.Type(OparlLocationDto::class, name = "https://schema.oparl.org/1.1/Location"),
    JsonSubTypes.Type(OparlMeetingDto::class, name = "https://schema.oparl.org/1.1/Meeting"),
    JsonSubTypes.Type(OparlMembershipDto::class, name = "https://schema.oparl.org/1.1/Membership"),
    JsonSubTypes.Type(OparlOrganizationDto::class, name = "https://schema.oparl.org/1.1/Organization"),
    JsonSubTypes.Type(OparlPaperDto::class, name = "https://schema.oparl.org/1.1/Paper"),
    JsonSubTypes.Type(OparlPersonDto::class, name = "https://schema.oparl.org/1.1/Person"),
    JsonSubTypes.Type(OparlSystemDto::class, name = "https://schema.oparl.org/1.1/System"),
)
sealed interface OparlObjectDto : OparlResponseDto {
    val id: String?
    val type: String?
}
