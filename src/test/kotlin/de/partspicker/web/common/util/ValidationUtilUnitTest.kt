package de.partspicker.web.common.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class ValidationUtilUnitTest : ShouldSpec({

    context("Boolean.orThrow") {
        should("just run when called while true") {
            // given & when & then
            true elseThrow Exception("Should not be thrown")
        }

        should("throw the given exception when called while false") {
            // given
            val message = "Something happened"

            // when
            val exception = shouldThrow<Exception> { false elseThrow Exception(message) }

            // then
            exception.message shouldBe message
        }
    }
})
