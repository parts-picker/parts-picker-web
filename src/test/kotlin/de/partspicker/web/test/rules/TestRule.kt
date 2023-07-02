package de.partspicker.web.test.rules

import de.partspicker.web.common.business.rules.Rule
import de.partspicker.web.common.util.elseThrow

class TestRule(private val valid: Boolean) : Rule {
    override fun valid() {
        valid elseThrow TestRuleException()
    }

    companion object {
        val VALID_RULE = TestRule(true)
        val INVALID_RULE = TestRule(false)
    }
}
