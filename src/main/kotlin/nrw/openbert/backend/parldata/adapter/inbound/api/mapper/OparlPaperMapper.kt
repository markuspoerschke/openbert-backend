package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaperDto
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.domain.entity.AbstractPaper
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedPaper
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.util.Date

@Component
class OparlPaperMapper(
    private val urlGenerator: OparlUrlGenerator,
    private val consultationMapper: OparlConsultationMapper,
) {
    fun toDto(paper: ResolvedPaper): OparlPaperDto {
        return OparlPaperDto(
            id = urlGenerator.paper(paper.body.id, paper.id),
            type = paper.getType().url,
            body = urlGenerator.body(paper.body.id),
            name = paper.name,
            reference = paper.reference,
            date = paper.date?.toOparlDate(),
            paperType = paper.paperType,
            consultation = paper.consultations
                .map { consultationMapper.toDto(it, includePaper = false) }
                .takeIf { it.isNotEmpty() },
            license = paper.license,
            keyword = paper.keyword.toList().takeIf { it.isNotEmpty() },
            created = paper.created.toString(),
            modified = paper.modified.toString(),
            web = paper.web,
            deleted = paper.deleted,
        )
    }

    private fun Date.toOparlDate(): String {
        return toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()
    }
}
