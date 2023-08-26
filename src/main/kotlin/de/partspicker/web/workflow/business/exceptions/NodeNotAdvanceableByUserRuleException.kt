package de.partspicker.web.workflow.business.exceptions

import de.partspicker.web.common.business.exceptions.RuleException

class NodeNotAdvanceableByUserRuleException(nodeType: String?) :
    RuleException("Node of type $nodeType cannot be advanced by a user")
