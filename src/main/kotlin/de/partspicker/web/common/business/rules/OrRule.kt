package de.partspicker.web.common.business.rules

import de.partspicker.web.common.business.exceptions.OrRuleException
import de.partspicker.web.common.business.exceptions.RuleException

class OrRule(private val firstRule: Rule, private val secondRule: Rule) : Rule {
    override fun valid() {
        val firstRuleException: RuleException
        try {
            firstRule.valid()
            return
        } catch (exception: RuleException) {
            firstRuleException = exception
        }

        val secondRuleException: RuleException
        try {
            secondRule.valid()
            return
        } catch (exception: RuleException) {
            secondRuleException = exception
        }

        throw OrRuleException(firstRuleException, secondRuleException)
    }
}

infix fun Rule.or(other: Rule) = OrRule(this, other)
