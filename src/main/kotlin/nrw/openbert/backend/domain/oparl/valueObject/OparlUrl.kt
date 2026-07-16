package nrw.openbert.backend.domain.oparl.valueObject

data class OparlUrl(
    val value: String
) {
    companion object {
        const val MAX_LENGTH = 1024
    }

    init {
        require(value.length <= MAX_LENGTH) { "URL cannot be longer than 1024 characters." }
    }
}
