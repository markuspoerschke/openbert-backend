package nrw.openbert.backend.parldata.application.port.outbound

import nrw.openbert.backend.parldata.application.port.model.FileContent
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import java.io.IOException

interface FileContentFetcher {
    /** The body is only valid during [consume]; the fetcher closes it even if consumption fails. */
    @Throws(IOException::class)
    fun fetch(url: OparlUrl, consume: (FileContent) -> Unit)
}
