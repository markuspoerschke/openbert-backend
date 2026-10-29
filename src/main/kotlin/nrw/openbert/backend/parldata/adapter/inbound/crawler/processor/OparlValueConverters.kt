package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Date

internal fun String.toOparlDate(): Date = Date.from(LocalDate.parse(this).atStartOfDay(ZoneOffset.UTC).toInstant())

internal fun String.toOparlDateTime(): Date = Date.from(OffsetDateTime.parse(this).toInstant())

internal fun String.toOparlUrl(): OparlUrl = OparlUrl(this)
