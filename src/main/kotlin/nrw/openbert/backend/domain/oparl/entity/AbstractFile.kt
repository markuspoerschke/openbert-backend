package nrw.openbert.backend.domain.oparl.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import nrw.openbert.backend.infrastructure.oparl.persitence.converter.OparlUrlConverter
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@MappedSuperclass
abstract class AbstractFile(id: OparlEntityId) : AbstractOparlEntity(id) {
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var keyword: Array<String> = emptyArray()

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
