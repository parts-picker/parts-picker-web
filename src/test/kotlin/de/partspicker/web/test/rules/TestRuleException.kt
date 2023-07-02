package de.partspicker.web.test.rules

import de.partspicker.web.common.business.exceptions.RuleException

class TestRuleException : RuleException(MESSAGE) {
    companion object {
        const val MESSAGE = "TestRuleException thrown"
    }
}
