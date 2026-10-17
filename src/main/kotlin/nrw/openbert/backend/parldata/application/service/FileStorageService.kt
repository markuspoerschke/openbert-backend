package nrw.openbert.backend.parldata.application.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.security.MessageDigest
import java.util.UUID

@Service
class FileStorageService(
    @Value("\${openbert.file.storage.path:/tmp/openbert-files}")
    private val storagePath: String,
) {
    private val storageDirectory: Path = Paths.get(storagePath)

    init {
        Files.createDirectories(storageDirectory)
    }

    fun storeFile(
        fileId: UUID,
        content: ByteArray,
        originalFileName: String? = null,
    ): Path {
        val fileExtension = originalFileName?.substringAfterLast('.', "") ?: ""
        val storedFileName = "$fileId${if (fileExtension.isNotEmpty()) ".$fileExtension" else ""}"
        val filePath = storageDirectory.resolve(storedFileName)

        Files.write(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
        return filePath
    }

    fun storeFile(
        fileId: UUID,
        inputStream: InputStream,
        originalFileName: String? = null,
    ): Path {
        val fileExtension = originalFileName?.substringAfterLast('.', "") ?: ""
        val storedFileName = "$fileId${if (fileExtension.isNotEmpty()) ".$fileExtension" else ""}"
        val filePath = storageDirectory.resolve(storedFileName)

        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
        return filePath
    }

    fun getFile(fileId: UUID, originalFileName: String? = null): ByteArray? {
        val fileExtension = originalFileName?.substringAfterLast('.', "") ?: ""
        val storedFileName = "$fileId${if (fileExtension.isNotEmpty()) ".$fileExtension" else ""}"
        val filePath = storageDirectory.resolve(storedFileName)

        return if (Files.exists(filePath)) {
            Files.readAllBytes(filePath)
        } else {
            null
        }
    }

    fun getFilePath(fileId: UUID, originalFileName: String? = null): Path {
        val fileExtension = originalFileName?.substringAfterLast('.', "") ?: ""
        val storedFileName = "$fileId${if (fileExtension.isNotEmpty()) ".$fileExtension" else ""}"
        return storageDirectory.resolve(storedFileName)
    }

    fun fileExists(fileId: UUID, originalFileName: String? = null): Boolean {
        val fileExtension = originalFileName?.substringAfterLast('.', "") ?: ""
        val storedFileName = "$fileId${if (fileExtension.isNotEmpty()) ".$fileExtension" else ""}"
        val filePath = storageDirectory.resolve(storedFileName)
        return Files.exists(filePath)
    }

    fun deleteFile(fileId: UUID, originalFileName: String? = null): Boolean {
        val fileExtension = originalFileName?.substringAfterLast('.', "") ?: ""
        val storedFileName = "$fileId${if (fileExtension.isNotEmpty()) ".$fileExtension" else ""}"
        val filePath = storageDirectory.resolve(storedFileName)

        return if (Files.exists(filePath)) {
            Files.delete(filePath)
            true
        } else {
            false
        }
    }

    fun verifyChecksum(content: ByteArray, expectedChecksum: String?): Boolean {
        if (expectedChecksum == null) {
            return true
        }

        val checksum = when (expectedChecksum.length) {
            40 -> computeSha1(content)
            128 -> computeSha512(content)
            else -> return false
        }

        return checksum.equals(expectedChecksum, ignoreCase = true)
    }

    fun computeSha1(content: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val hash = digest.digest(content)
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun computeSha512(content: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-512")
        val hash = digest.digest(content)
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun cleanupOldFiles(maxAgeHours: Long = 24) {
        val cutoffTime = System.currentTimeMillis() - (maxAgeHours * 60 * 60 * 1000)
        storageDirectory.toFile().listFiles()?.forEach { file ->
            if (file.lastModified() < cutoffTime) {
                file.delete()
            }
        }
    }
}
