package nrw.openbert.backend.bootstrap

import org.springframework.security.crypto.encrypt.BytesEncryptor
import org.springframework.security.crypto.encrypt.TextEncryptor
import kotlin.io.encoding.Base64

class TextEncrypterAdapter(
    val inner: BytesEncryptor,
) : TextEncryptor {
    override fun encrypt(text: String): String = Base64.encode(inner.encrypt(text.toByteArray(Charsets.UTF_8)))

    override fun decrypt(encryptedText: String): String =
        inner.decrypt(Base64.decode(encryptedText)).toString(Charsets.UTF_8)
}
