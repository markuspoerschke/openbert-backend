package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlConsultationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlConsultationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedConsultationRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(
    name = "Oparl Consultation",
    externalDocs = ExternalDocumentation(
        "Oparl Specs",
        "https://oparl.org/spezifikation/online-ansicht/#entity-consultation",
    ),
)
@RestController
class OparlConsultationController(
    private val urlGenerator: OparlUrlGenerator,
    private val consultationMapper: OparlConsultationMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedConsultationRepository: ResolvedConsultationRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/consultation")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlConsultationDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val consultations = resolvedConsultationRepository
            .findAllByBodyIdOrderById(bodyId, pagination.toPageable())
            .map(consultationMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.consultation.list(bodyId, pagination.page),
                data = consultations.toList(),
                pagination = listPaginationMapper.toDto(consultations),
                links = listLinksMapper.toDto(
                    consultations,
                    { page -> urlGenerator.consultation.list(bodyId, page) }
                ),
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/consultation/{consultationId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable consultationId: UUID,
    ): ResponseEntity<OparlConsultationDto> {
        val consultation = resolvedConsultationRepository.findByIdAndBodyId(
            consultationId,
            bodyId,
        )

        if (!consultation.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(consultationMapper.toDto(consultation.get()))
    }
}
