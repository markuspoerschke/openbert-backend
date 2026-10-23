package nrw.openbert.backend.crawl.adapter.client

import com.sun.net.httpserver.HttpServer
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.OparlFileContentClient
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.http.client.MockClientHttpResponse
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.web.client.RestClient
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OparlFileContentClientTest {
    private val builder = RestClient.builder()
    private val server = MockRestServiceServer.bindTo(builder).build()
    private val client = OparlFileContentClient(builder)
    private val url = OparlUrl("https://oparl.example.org/files/a%20document.pdf?signature=a%2Fb")

    @AfterEach
    fun verifyUpstream() {
        server.verify()
    }

    @Test
    fun `copies a large response in bounded chunks and closes it after consumption`() {
        val source = GeneratedStream(16 * 1024 * 1024)
        val response = MockClientHttpResponse(source, HttpStatus.OK)
        response.headers.contentType = MediaType.APPLICATION_PDF
        response.headers.contentLength = source.size.toLong()
        server.expect(requestTo(url.value)).andRespond { response }
        var bytesWritten = 0
        val sink = object : OutputStream() {
            override fun write(value: Int) {
                bytesWritten++
            }

            override fun write(bytes: ByteArray, offset: Int, length: Int) {
                assertTrue(length <= DEFAULT_BUFFER_SIZE)
                bytesWritten += length
            }
        }

        client.fetch(url) { content ->
            assertFalse(source.closed)
            assertEquals(0, source.bytesRead)
            assertEquals(200, content.statusCode)
            assertEquals("application/pdf", content.contentType)
            assertEquals(source.size.toLong(), content.contentLength)
            content.body.copyTo(sink)
            assertFalse(source.closed)
        }

        assertEquals(source.size, bytesWritten)
        assertTrue(source.closed)
    }

    @Test
    fun `closes upstream when the downstream client disconnects`() {
        val source = GeneratedStream(1024 * 1024)
        server.expect(requestTo(url.value))
            .andRespond { MockClientHttpResponse(source, HttpStatus.OK) }

        assertFailsWith<IOException> {
            client.fetch(url) { content ->
                content.body.copyTo(object : OutputStream() {
                    override fun write(value: Int) {
                        throw IOException("Client disconnected")
                    }
                })
            }
        }

        assertTrue(source.closed)
        assertTrue(source.bytesRead < source.size)
    }

    @Test
    fun `exposes error status without buffering the upstream error body`() {
        val source = GeneratedStream(1024 * 1024)
        server.expect(requestTo(url.value))
            .andRespond { MockClientHttpResponse(source, HttpStatus.GONE) }

        client.fetch(url) { content ->
            assertEquals(410, content.statusCode)
            assertEquals(0, source.bytesRead)
        }

        assertTrue(source.closed)
    }

    @Test
    fun `rejects malformed or unsupported upstream urls`() {
        for (value in listOf("not a URL", "file:///etc/passwd", "https:/missing-host")) {
            assertFailsWith<IOException> {
                client.fetch(OparlUrl(value)) { error("Invalid URLs must not be fetched") }
            }
        }
    }

    @Test
    fun `follows upstream redirects using the application default request factory`() {
        val bytes = byteArrayOf(0x25, 0x50, 0x44, 0x46, 0, 0xFF.toByte())
        val upstream = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        upstream.createContext("/redirect") { exchange ->
            exchange.responseHeaders.set(HttpHeaders.LOCATION, "/document")
            exchange.sendResponseHeaders(302, -1)
            exchange.close()
        }
        upstream.createContext("/document") { exchange ->
            exchange.responseHeaders.set(HttpHeaders.CONTENT_TYPE, "application/pdf")
            exchange.sendResponseHeaders(200, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
            exchange.close()
        }
        upstream.start()

        try {
            val redirectClient = OparlFileContentClient(
                RestClient.builder().requestFactory(ClientHttpRequestFactoryBuilder.jdk().build())
            )
            redirectClient.fetch(OparlUrl("http://127.0.0.1:${upstream.address.port}/redirect")) { content ->
                assertEquals(200, content.statusCode)
                assertContentEquals(bytes, content.body.readBytes())
            }
        } finally {
            upstream.stop(0)
        }
    }

    private class GeneratedStream(val size: Int) : InputStream() {
        var bytesRead = 0
        var closed = false

        override fun read(): Int = if (bytesRead < size) {
            bytesRead++
            0x42
        } else {
            -1
        }

        override fun read(bytes: ByteArray, offset: Int, length: Int): Int {
            val count = minOf(length, size - bytesRead)
            if (count == 0) return -1
            bytes.fill(0x42, offset, offset + count)
            bytesRead += count
            return count
        }

        override fun close() {
            closed = true
        }
    }
}
