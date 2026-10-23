package nrw.openbert.backend.parldata.application.port.model

import java.io.InputStream

data class FileContent(
    val statusCode: Int,
    val contentType: String?,
    val contentLength: Long?,
    val body: InputStream,
)
