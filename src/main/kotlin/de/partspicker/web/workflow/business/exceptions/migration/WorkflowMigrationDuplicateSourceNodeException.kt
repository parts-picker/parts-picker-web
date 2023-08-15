package de.partspicker.web.workflow.business.exceptions.migration

class WorkflowMigrationDuplicateSourceNodeException(sourceNodeNames: Iterable<String>) : WorkflowMigrationException(
    "Two or more node migrations may not have the same source node name: $sourceNodeNames"
)
