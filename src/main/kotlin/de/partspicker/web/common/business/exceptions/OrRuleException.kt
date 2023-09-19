package de.partspicker.web.common.business.exceptions

class OrRuleException(
    firstRule: RuleException,
    secondRule: RuleException,
    vararg otherExceptions: RuleException
) : RuleException(MESSAGE) {
    companion object {
        const val MESSAGE = "At least one of the given rules must be valid, but none were"
    }

    val exceptions = otherExceptions.toList() + firstRule + secondRule
}
