package nrw.openbert.backend.bootstrap

import org.springframework.security.crypto.encrypt.BytesEncryptor
import org.springframework.security.crypto.encrypt.TextEncryptor

class TextEncrypterAdapter(val inner: BytesEncryptor) : TextEncryptor {
    override fun encrypt(text: String): String {
        return inner.encrypt(text.toByteArray()).contentToString()
    }

    override fun decrypt(encryptedText: String): String {
        return inner.decrypt(encryptedText.toByteArray()).contentToString()
    }
}
