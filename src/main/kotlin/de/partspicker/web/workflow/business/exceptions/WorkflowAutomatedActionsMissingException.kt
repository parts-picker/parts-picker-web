package de.partspicker.web.workflow.business.exceptions

class WorkflowAutomatedActionsMissingException(workflowName: String, actionNames: Iterable<String>) : RuntimeException(
    "The workflow named '$workflowName' cannot be created " +
        "as the requested automated actions named '$actionNames' are missing"
)
