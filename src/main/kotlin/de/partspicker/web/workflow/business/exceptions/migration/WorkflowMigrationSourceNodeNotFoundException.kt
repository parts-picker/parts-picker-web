package de.partspicker.web.workflow.business.exceptions.migration

class WorkflowMigrationSourceNodeNotFoundException(
    missingSourceNodeNames: Iterable<String>
) : WorkflowMigrationException(
    "The source node(s) with the name(s) '$missingSourceNodeNames' could not be found for the given migration plan."
)
