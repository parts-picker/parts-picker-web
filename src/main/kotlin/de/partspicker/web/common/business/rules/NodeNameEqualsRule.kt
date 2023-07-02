package de.partspicker.web.common.business.rules

import de.partspicker.web.common.business.exceptions.WrongNodeNameRuleException
import de.partspicker.web.common.util.elseThrow

class NodeNameEqualsRule(private val currentNodeName: String?, private val expectedNodeName: String) : Rule {
    override fun valid() {
        (currentNodeName == expectedNodeName) elseThrow WrongNodeNameRuleException(currentNodeName, expectedNodeName)
    }
}
