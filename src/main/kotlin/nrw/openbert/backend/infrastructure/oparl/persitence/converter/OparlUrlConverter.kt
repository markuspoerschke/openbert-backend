package nrw.openbert.backend.infrastructure.oparl.persitence.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl

@Converter
class OparlUrlConverter : AttributeConverter<OparlUrl, String> {
    override fun convertToDatabaseColumn(attribute: OparlUrl?): String? {
        return attribute?.value
    }

    override fun convertToEntityAttribute(dbData: String?): OparlUrl? {
        return dbData?.let { OparlUrl(it) }
    }
}