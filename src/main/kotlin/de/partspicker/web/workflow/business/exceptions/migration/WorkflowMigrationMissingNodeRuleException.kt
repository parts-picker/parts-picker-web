package de.partspicker.web.workflow.business.exceptions.migration

class WorkflowMigrationMissingNodeRuleException(
    migrationPlanId: Long,
    missingSourceNodeName: String
) : WorkflowMigrationException(
    "The given migration plan with id $migrationPlanId is " +
        "missing a rule for the source node with name $missingSourceNodeName"
)
