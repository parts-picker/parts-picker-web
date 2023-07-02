package de.partspicker.web.common.business.rules

import de.partspicker.web.common.business.exceptions.WrongNodeNameRuleException
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows

class NodeNameEqualsRuleUnitTest : ShouldSpec({

    should("just run when expected equals current node name") {
        // given
        val expected = "test"
        val current = "test"

        val rule = NodeNameEqualsRule(current, expected)

        // when & then
        rule.valid()
    }

    should("throw WrongNodeNameRuleException when expected not equal current node name") {
        // given
        val expected = "test"
        val current = "not-test"

        val rule = NodeNameEqualsRule(current, expected)

        // when & then
        val exception = assertThrows<WrongNodeNameRuleException> { rule.valid() }

        exception.message shouldBe "Expected project status is $expected, but actual status is $current"
    }
})
