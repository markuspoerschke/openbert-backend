package nrw.openbert.backend.oparl.infrastructure.api.v1.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping
import io.swagger.v3.oas.annotations.media.Schema

data class OparlSearchResultListDto(
    val id: String,

    @field:ArraySchema(
        schema = Schema(
            discriminatorProperty = "type",
            discriminatorMapping = [
                DiscriminatorMapping(value = OparlSystemDto.TYPE, schema = OparlSystemDto::class),
                DiscriminatorMapping(value = OparlBodyDto.TYPE, schema = OparlBodyDto::class),
                DiscriminatorMapping(
                    value = OparlLegislativeTermDto.TYPE,
                    schema = OparlLegislativeTermDto::class,
                ),
                DiscriminatorMapping(
                    value = OparlOrganizationDto.TYPE,
                    schema = OparlOrganizationDto::class,
                ),
                DiscriminatorMapping(value = OparlPersonDto.TYPE, schema = OparlPersonDto::class),
                DiscriminatorMapping(
                    value = OparlMembershipDto.TYPE,
                    schema = OparlMembershipDto::class,
                ),
                DiscriminatorMapping(value = OparlMeetingDto.TYPE, schema = OparlMeetingDto::class),
                DiscriminatorMapping(
                    value = OparlAgendaItemDto.TYPE,
                    schema = OparlAgendaItemDto::class,
                ),
                DiscriminatorMapping(value = OparlPaperDto.TYPE, schema = OparlPaperDto::class),
                DiscriminatorMapping(
                    value = OparlConsultationDto.TYPE,
                    schema = OparlConsultationDto::class,
                ),
                DiscriminatorMapping(value = OparlFileDto.TYPE, schema = OparlFileDto::class),
                DiscriminatorMapping(value = OparlLocationDto.TYPE, schema = OparlLocationDto::class),
            ],
            oneOf = [
                OparlSystemDto::class,
                OparlBodyDto::class,
                OparlLegislativeTermDto::class,
                OparlOrganizationDto::class,
                OparlPersonDto::class,
                OparlMembershipDto::class,
                OparlMeetingDto::class,
                OparlAgendaItemDto::class,
                OparlPaperDto::class,
                OparlConsultationDto::class,
                OparlFileDto::class,
                OparlLocationDto::class,
            ]
        )
    )
    val data: List<Any>,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val pagination: OparlListPaginationDto? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val links: OparlListLinksDto? = null,
) {
    val type: String = TYPE

    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/List"
    }
}