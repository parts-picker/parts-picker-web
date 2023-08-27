package de.partspicker.web.workflow.business.rules

import de.partspicker.web.common.business.rules.Rule
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.workflow.business.exceptions.NodeNotAdvanceableByUserRuleException
import de.partspicker.web.workflow.business.objects.nodes.Node
import de.partspicker.web.workflow.business.objects.nodes.StartNode
import de.partspicker.web.workflow.business.objects.nodes.UserActionNode

class UserMayAdvanceNodeRule(private val currentNode: Node) : Rule {
    override fun valid() {
        (currentNode is UserActionNode || currentNode is StartNode) elseThrow
            NodeNotAdvanceableByUserRuleException(currentNode.javaClass.simpleName)
    }
}
