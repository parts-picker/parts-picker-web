package de.partspicker.web.workflow.business.exceptions.migration

class WorkflowMigrationDuplicatedInstanceValueKeyException(
    duplicatedKeys: Iterable<String>,
    sourceNode: String,
    targetNode: String
) :
    WorkflowMigrationException(
        "Keys must be unique within a node migration rule but there are duplicates: $duplicatedKeys " +
            "for rule '$sourceNode' --> '$targetNode'"
    )
