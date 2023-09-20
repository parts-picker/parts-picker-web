package de.partspicker.web.common.util

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

fun Pageable.withSort(sort: Sort): Pageable {
    return PageRequest.of(this.pageNumber, this.pageSize, sort)
}

/**
 * Returns a new otherwise identical pageable with the given sort if the current sort is unsorted.
 */
fun Pageable.withDefaultSort(sort: Sort): Pageable {
    return if (this.sort.isUnsorted && this != Pageable.unpaged()) {
        this.withSort(sort)
    } else {
        this
    }
}
