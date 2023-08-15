package de.partspicker.web.workflow.business.exceptions.migration

class WorkflowMigrationImplicitNodeRuleCreationException(nodeName: String) : WorkflowMigrationException(
    "Implicit migration rule for the node with name '$nodeName' cannot be created, explicit migration rule is needed"
)
