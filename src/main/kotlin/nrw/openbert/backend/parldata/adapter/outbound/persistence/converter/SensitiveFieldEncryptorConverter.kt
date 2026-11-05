package nrw.openbert.backend.parldata.adapter.outbound.persistence.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import nrw.openbert.backend.parldata.domain.valueObject.AuthorizationHeader
import org.springframework.security.crypto.encrypt.TextEncryptor

@Converter(autoApply = true)
class SensitiveFieldEncryptorConverter(
    val textEncryptor: TextEncryptor,
) : AttributeConverter<AuthorizationHeader?, String?> {
    override fun convertToDatabaseColumn(attribute: AuthorizationHeader?): String? {
        if (attribute == null) {
            return null
        }

        return textEncryptor.encrypt(attribute.value)
    }

    override fun convertToEntityAttribute(dbData: String?): AuthorizationHeader? {
        if (dbData == null) {
            return null
        }

        return AuthorizationHeader(textEncryptor.decrypt(dbData))
    }
}
