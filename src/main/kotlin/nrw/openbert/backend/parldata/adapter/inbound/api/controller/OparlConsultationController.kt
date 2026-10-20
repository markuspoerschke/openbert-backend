package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlConsultationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlConsultationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedConsultationRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

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
        @RequestParam page: Int = 1,
    ): ResponseEntity<OparlListDto<OparlConsultationDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val consultations = resolvedConsultationRepository
            // please note that we need to subtract 1 from the page number because oparl expects a 1-based page number
            .findAllByBody_IdOrderById(bodyId, PageRequest.of(page - 1, PAGE_SIZE))
            .map(consultationMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.consultationList(bodyId, page),
                data = consultations.toList(),
                pagination = listPaginationMapper.toDto(consultations),
                links = listLinksMapper.toDto(
                    consultations,
                    { page -> urlGenerator.consultationList(bodyId, page) }
                ),
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/consultation/{consultationId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable consultationId: UUID,
    ): ResponseEntity<OparlConsultationDto> {
        val consultation = resolvedConsultationRepository.findByIdAndBody_Id(
            consultationId,
            bodyId,
        )

        if (!consultation.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(consultationMapper.toDto(consultation.get()))
    }

    companion object {
        private const val PAGE_SIZE = 25
    }
}
