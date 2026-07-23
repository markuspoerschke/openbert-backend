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
    private var name: String? = null

    @Column
    private var fileName: String? = null

    @Column
    private var mimeType: String? = null

    @Column
    private var date: Date? = null

    @Column
    private var size: Int? = null

    @Column
    private var sha1Checksum: String? = null

    @Column
    private var sha512Checksum: String? = null

    @Column
    private var text: String? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    private var accessUrl: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    private var downloadUrl: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    private var externalServiceUrl: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    private var fileLicense: OparlUrl? = null

    @Column
    private var license: String? = null

    @Column(columnDefinition = "text[]")
    private var keyword: MutableList<String> = mutableListOf()

    @Column
    private var web: String? = null

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
