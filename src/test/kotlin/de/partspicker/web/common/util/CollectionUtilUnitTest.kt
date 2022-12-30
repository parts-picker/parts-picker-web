package de.partspicker.web.common.util

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class CollectionUtilUnitTest : ShouldSpec({

    context("intersects") {
        should("return true when two collections share an element") {
            // given
            val list1 = listOf(1, 2, 3)
            val list2 = listOf(3, 4, 5)

            // when
            val result = list1.intersects(list2)

            // then
            result shouldBe true
        }

        should("return false when two collections share no elements") {
            // given
            val list1 = listOf(1, 2, 3)
            val list2 = listOf(4, 5, 6)

            // when
            val result = list1.intersects(list2)

            // then
            result shouldBe false
        }
    }

    context("duplicates") {
        should("return an empty set if the list has no duplicates") {
            // given
            val list = listOf(1, 2, 3)

            // when
            val result = list.duplicates()

            // then
            result shouldBe emptySet()
        }

        should("return a set with all duplicated value") {
            // given
            val list = listOf(1, 2, 2, 3, 3)

            // when
            val result = list.duplicates()

            // then
            result shouldBe listOf(2, 3)
        }
    }
})
