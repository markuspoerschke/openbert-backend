package nrw.openbert.backend.oparl.infrastructure.api.v1.mapper

import nrw.openbert.backend.oparl.domain.entity.AbstractPaper
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedPaper
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlPaperDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.util.Date

@Component
class OparlPaperMapper(
    private val urlGenerator: OparlUrlGenerator,
) {
    fun toDto(paper: ResolvedPaper): OparlPaperDto {
        return OparlPaperDto(
            id = urlGenerator.paper(paper.body.id, paper.id),
            type = AbstractPaper.TYPE,
            body = urlGenerator.body(paper.body.id),
            name = paper.name,
            reference = paper.reference,
            date = paper.date?.toOparlDate(),
            paperType = paper.paperType,
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
