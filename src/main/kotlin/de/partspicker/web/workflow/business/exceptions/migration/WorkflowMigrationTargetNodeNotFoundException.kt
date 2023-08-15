package de.partspicker.web.workflow.business.exceptions.migration

class WorkflowMigrationTargetNodeNotFoundException(missingTargetNodeNames: List<String>) : WorkflowMigrationException(
    "The target node(s) with the name(s) '$missingTargetNodeNames' could not be found for the given migration plan."
)
