package nrw.openbert.backend.parldata.adapter.outbound.persistence.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl

@Converter(autoApply = true)
class OparlUrlConverter : AttributeConverter<OparlUrl, String> {
    override fun convertToDatabaseColumn(attribute: OparlUrl?): String? {
        return attribute?.value
    }

    override fun convertToEntityAttribute(dbData: String?): OparlUrl? {
        return dbData?.let { OparlUrl(it) }
    }
}
