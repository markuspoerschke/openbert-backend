package nrw.openbert.backend.parldata.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import nrw.openbert.backend.parldata.adapter.outbound.persistence.converter.OparlUrlConverter
import nrw.openbert.backend.parldata.domain.enums.OparlType
import java.util.*

@MappedSuperclass
abstract class AbstractFile(resource: OparlResource) : AbstractOparlEntity(resource) {
    @Column(length = 500)
    var name: String? = null

    @Column
    var fileName: String? = null

    @Column
    var mimeType: String? = null

    @Column
    var date: Date? = null

    @Column
    var size: Int? = null

    @Column(name = "sha1_checksum")
    var sha1Checksum: String? = null

    @Column(name = "sha512_checksum")
    var sha512Checksum: String? = null

    @Column(columnDefinition = "TEXT")
    var text: String? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var accessUrl: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var downloadUrl: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var externalServiceUrl: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var fileLicense: OparlUrl? = null

    @Column
    var license: String? = null

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()

    @Column
    var web: String? = null

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

    override fun getType() = OparlType.FILE
}
