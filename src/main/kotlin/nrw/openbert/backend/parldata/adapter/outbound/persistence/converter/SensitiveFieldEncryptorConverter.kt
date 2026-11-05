package nrw.openbert.backend.parldata.adapter.outbound.persistence.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.security.crypto.encrypt.TextEncryptor

@Converter
class SensitiveFieldEncryptorConverter(val textEncryptor: TextEncryptor) : AttributeConverter<String?, String?> {
    override fun convertToDatabaseColumn(attribute: String?): String? {
        if (attribute == null) {
            return null
        }

        return textEncryptor.encrypt(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        if (dbData == null) {
            return null
        }

        return textEncryptor.decrypt(dbData)
    }
}
