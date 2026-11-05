package nrw.openbert.backend.bootstrap

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.encrypt.AesGcmBytesEncryptor
import org.springframework.security.crypto.encrypt.TextEncryptor
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

@Configuration
class CryptoConfiguration {

    @Bean
    fun textEncryptor(
        @Value("\${app.crypto.key-base64}")
        keyBase64: String,
    ): TextEncryptor {
        val decodedSecretKeyBytes = Base64.decode(keyBase64)
        val secretKey = SecretKeySpec(decodedSecretKeyBytes, "AES")

        return TextEncrypterAdapter(
            AesGcmBytesEncryptor
                .withSecretKey(secretKey)
                .build(),
        )
    }
}
