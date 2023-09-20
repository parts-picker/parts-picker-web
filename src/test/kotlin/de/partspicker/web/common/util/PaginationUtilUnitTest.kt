package de.partspicker.web.common.util

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class PaginationUtilUnitTest : ShouldSpec({

    context("withSort") {

        data class WithSort(
            val sort: Sort,
        ) : WithDataTestName {
            override fun dataTestName() = "should return pageable with given sort $sort"
        }

        withData(
            WithSort(Sort.unsorted()),
            WithSort(Sort.by("id")),
            WithSort(Sort.by("item.id"))
        ) {
                (sort) ->
            // given
            val originalPageable = PageRequest.of(0, 10, Sort.by("anything.else"))

            // when
            val pageableWithSort = originalPageable.withSort(sort)

            // then
            pageableWithSort.pageNumber shouldBe originalPageable.pageNumber
            pageableWithSort.pageSize shouldBe originalPageable.pageSize
            pageableWithSort.sort shouldBe sort
        }
    }

    context("withDefaultSort") {

        data class WithDefaultSort(
            val originalSort: Sort,
            val givenSort: Sort,
            val expectedSort: Sort
        ) : WithDataTestName {
            override fun dataTestName() =
                "should return pageable with sort $expectedSort when given sort $givenSort & " +
                    "sort of pageable was $originalSort"
        }

        withData(
            WithDefaultSort(Sort.unsorted(), Sort.by("id"), Sort.by("id")),
            WithDefaultSort(Sort.by("id"), Sort.by("something.else"), Sort.by("id")),
        ) {
                (originalSort, givenSort, expectedSort) ->
            // given
            val originalPageable = PageRequest.of(0, 10, originalSort)

            // when
            val pageableWithSort = originalPageable.withDefaultSort(givenSort)

            // then
            pageableWithSort.pageNumber shouldBe originalPageable.pageNumber
            pageableWithSort.pageSize shouldBe originalPageable.pageSize
            pageableWithSort.sort shouldBe expectedSort
        }
    }
})
