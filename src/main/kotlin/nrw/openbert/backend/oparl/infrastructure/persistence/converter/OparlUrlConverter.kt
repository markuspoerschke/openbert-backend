package nrw.openbert.backend.oparl.infrastructure.persistence.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl

@Converter
class OparlUrlConverter : AttributeConverter<OparlUrl, String> {
    override fun convertToDatabaseColumn(attribute: OparlUrl?): String? {
        return attribute?.value
    }

    override fun convertToEntityAttribute(dbData: String?): OparlUrl? {
        return dbData?.let { OparlUrl(it) }
    }
}
