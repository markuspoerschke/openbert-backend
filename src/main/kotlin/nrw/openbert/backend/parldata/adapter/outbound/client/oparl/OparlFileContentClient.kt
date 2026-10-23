package nrw.openbert.backend.parldata.adapter.outbound.client.oparl

import nrw.openbert.backend.parldata.application.port.model.FileContent
import nrw.openbert.backend.parldata.application.port.outbound.FileContentFetcher
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import java.io.IOException
import java.net.URI

@Component
class OparlFileContentClient(restClientBuilder: RestClient.Builder) : FileContentFetcher {
    private val restClient = restClientBuilder.build()

    override fun fetch(url: OparlUrl, consume: (FileContent) -> Unit) {
        val uri = try {
            URI.create(url.value).also {
                require(it.scheme in setOf("http", "https") && it.host != null)
            }
        } catch (exception: IllegalArgumentException) {
            throw IOException("Invalid upstream file URL", exception)
        }

        try {
            // exchange keeps the response open throughout the callback and closes it in finally.
            restClient.get().uri(uri).exchange { _, response ->
                consume(
                    FileContent(
                        statusCode = response.statusCode.value(),
                        contentType = response.headers.getFirst(HttpHeaders.CONTENT_TYPE),
                        contentLength = response.headers.contentLength.takeIf { it >= 0 },
                        body = response.body,
                    )
                )
            }
        } catch (exception: RestClientException) {
            throw IOException("Unable to fetch upstream file", exception)
        }
    }
}
