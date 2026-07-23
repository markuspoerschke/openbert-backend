package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import nrw.openbert.backend.oparl.infrastructure.persitence.converter.OparlUrlConverter
import java.util.*

@MappedSuperclass
abstract class AbstractFile(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/File"
    }

    @Column
    var name: String? = null
        protected set

    @Column
    var fileName: String? = null
        protected set

    @Column
    var mimeType: String? = null
        protected set

    @Column
    var date: Date? = null
        protected set

    @Column
    var size: Int? = null
        protected set

    @Column
    var sha1Checksum: String? = null
        protected set

    @Column
    var sha512Checksum: String? = null
        protected set

    @Column
    var text: String? = null
        protected set

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var accessUrl: OparlUrl? = null
        protected set

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var downloadUrl: OparlUrl? = null
        protected set

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var externalServiceUrl: OparlUrl? = null
        protected set

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var fileLicense: OparlUrl? = null
        protected set

    @Column
    var license: String? = null
        protected set

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()
        protected set

    @Column
    var web: String? = null
        protected set

    fun applyValuesFrom(other: AbstractFile) {
        name = other.name
        fileName = other.fileName
        mimeType = other.mimeType
        date = other.date
        size = other.size
        sha1Checksum = other.sha1Checksum
        sha512Checksum = other.sha512Checksum
        text = other.text
        accessUrl = other.accessUrl
        downloadUrl = other.downloadUrl
        externalServiceUrl = other.externalServiceUrl
        fileLicense = other.fileLicense
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
