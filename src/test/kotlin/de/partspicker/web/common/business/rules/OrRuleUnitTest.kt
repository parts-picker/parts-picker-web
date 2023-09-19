package de.partspicker.web.common.business.rules

import de.partspicker.web.common.business.exceptions.OrRuleException
import de.partspicker.web.test.rules.TestRule.Companion.INVALID_RULE
import de.partspicker.web.test.rules.TestRule.Companion.VALID_RULE
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class OrRuleUnitTest : ShouldSpec({

    context("valid") {
        should("just run when one rule is valid") {
            // given
            val first = VALID_RULE
            val second = INVALID_RULE

            val orRule = OrRule(first, second)

            // when & then
            orRule.valid()
        }

        should("just run when both rules are valid") {
            // given
            val first = VALID_RULE
            val second = VALID_RULE

            val orRule = OrRule(first, second)

            // when & then
            orRule.valid()
        }

        should("throw OrRuleException when given two invalid rules") {
            // given
            val first = INVALID_RULE
            val second = INVALID_RULE

            val orRule = OrRule(first, second)

            // when
            val exception = shouldThrow<OrRuleException> {
                orRule.valid()
            }

            // then
            exception.message shouldBe OrRuleException.MESSAGE
            exception.exceptions shouldHaveSize 2
        }
    }
})
