
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

require(args.size == 3) {
    "Usage: crypto.main.kts <encrypt|decrypt> <base64-key> <text>"
}

val (action, keyBase64, text) = args.toList()

val keyBytes = Base64.getDecoder().decode(keyBase64)
require(keyBytes.size == 32) { "AES-256 requires a 32-byte key" }

val key = SecretKeySpec(keyBytes, "AES")

when (action) {
    "encrypt" -> {
        val iv = ByteArray(16).also { SecureRandom().nextBytes(it) }

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))

        val encrypted = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
        println(Base64.getEncoder().encodeToString(iv + encrypted))
    }

    "decrypt" -> {
        val data = Base64.getDecoder().decode(text)
        require(data.size >= 32) { "Invalid encrypted data" }

        val iv = data.copyOfRange(0, 16)
        val encrypted = data.copyOfRange(16, data.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))

        println(String(cipher.doFinal(encrypted), Charsets.UTF_8))
    }

    else -> error("Unknown action: $action")
}
