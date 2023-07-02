package de.partspicker.web.common.business.rules

import de.partspicker.web.test.rules.TestRule.Companion.INVALID_RULE
import de.partspicker.web.test.rules.TestRule.Companion.VALID_RULE
import de.partspicker.web.test.rules.TestRuleException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify

class AndRuleUnitTest : ShouldSpec({
    should("just run when given two valid sub rules") {
        // given
        val first = VALID_RULE
        val second = VALID_RULE

        val andRule = AndRule(first, second)

        // when & then
        andRule.valid()
    }

    should("just run when given multiple valid sub rules") {
        // given
        val first = VALID_RULE
        val second = VALID_RULE
        val third = VALID_RULE
        val fourth = VALID_RULE

        val andRule = AndRule(first, second, third, fourth)

        // when & then
        andRule.valid()
    }

    should("let sub rule throw TestRuleException if one sub rule invalid") {
        // given
        val first = VALID_RULE
        val second = INVALID_RULE

        val andRule = AndRule(first, second)

        // when & then
        shouldThrow<TestRuleException> {
            andRule.valid()
        }
    }

    should("let sub rule throw TestRuleException if multiple sub rules invalid") {
        // given
        val first = VALID_RULE
        val second = VALID_RULE
        val third = INVALID_RULE
        val fourth = INVALID_RULE

        val andRule = AndRule(first, second, third, fourth)

        // when & then
        shouldThrow<TestRuleException> {
            andRule.valid()
        }
    }

    should("call all rule valid methods if given multiple sub rules") {
        // given
        val first = mockk<Rule>()
        val second = mockk<Rule>()
        val third = mockk<Rule>()

        every { first.valid() } just runs
        every { second.valid() } just runs
        every { third.valid() } just runs

        val andRule = AndRule(first, second, third)

        // when
        andRule.valid()

        // then
        verify(exactly = 1) {
            first.valid()
            second.valid()
            third.valid()
        }
    }
})
