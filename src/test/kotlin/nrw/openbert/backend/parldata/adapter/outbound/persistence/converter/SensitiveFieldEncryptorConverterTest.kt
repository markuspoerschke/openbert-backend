package nrw.openbert.backend.parldata.adapter.outbound.persistence.converter

import nrw.openbert.backend.bootstrap.TextEncrypterAdapter
import nrw.openbert.backend.parldata.domain.valueObject.AuthorizationHeader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.encrypt.AesGcmBytesEncryptor
import org.springframework.security.crypto.encrypt.RsaSecretEncryptor
import javax.crypto.spec.SecretKeySpec

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
        val originalValue = AuthorizationHeader("my secret information")

        val dbValue = converter.convertToDatabaseColumn(originalValue)
        assertNotNull(dbValue)
        assertNotEquals(dbValue, originalValue.value)

        val decryptedValue = converter.convertToEntityAttribute(dbValue)
        assertEquals(originalValue, decryptedValue)
    }

    @Test
    fun `round trips through the production aes-gcm adapter and stores base64`() {
        val key = SecretKeySpec(ByteArray(32) { it.toByte() }, "AES")
        val encryptor = TextEncrypterAdapter(AesGcmBytesEncryptor.withSecretKey(key).build())
        val converter = SensitiveFieldEncryptorConverter(encryptor)
        val originalValue = AuthorizationHeader("Bearer my secret token")

        val dbValue = requireNotNull(converter.convertToDatabaseColumn(originalValue))
        assertNotEquals(dbValue, originalValue.value)
        assertTrue(dbValue.matches(Regex("[A-Za-z0-9+/=]+")), "ciphertext must be stored as Base64")

        assertEquals(originalValue, converter.convertToEntityAttribute(dbValue))
    }
}
