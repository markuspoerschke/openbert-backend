package nrw.openbert.backend.parldata.adapter.outbound.persistence.converter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.encrypt.RsaSecretEncryptor

class SensitiveFieldEncryptorConverterTest {
    val encryptor = RsaSecretEncryptor()

    @Test
    fun `null in database results into null`() {
        assertNull(SensitiveFieldEncryptorConverter(encryptor).convertToEntityAttribute(null))
    }

    @Test
    fun `null in entity results into null`() {
        assertNull(SensitiveFieldEncryptorConverter(encryptor).convertToDatabaseColumn(null))
    }

    @Test
    fun `can encrypt and decrypt`() {
        val converter = SensitiveFieldEncryptorConverter(encryptor)
        val originalValue = "my secret information"

        val dbValue = converter.convertToDatabaseColumn(originalValue)
        assertNotNull(dbValue)
        assertNotEquals(dbValue, originalValue)

        val decryptedValue = converter.convertToEntityAttribute(dbValue)
        assertEquals(originalValue, decryptedValue)
    }
}
