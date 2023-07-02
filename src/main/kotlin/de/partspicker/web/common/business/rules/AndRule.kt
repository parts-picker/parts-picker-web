package de.partspicker.web.common.business.rules

class AndRule(private val firstRule: Rule, private val secondRule: Rule, private vararg val rules: Rule) : Rule {
    override fun valid() {
        firstRule.valid()
        secondRule.valid()
        rules.forEach { it.valid() }
    }
}
