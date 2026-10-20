package nrw.openbert.backend.parldata.domain.extension

import org.springframework.data.domain.Page

val Page<*>.numberOneBased: Int get() = number + 1
