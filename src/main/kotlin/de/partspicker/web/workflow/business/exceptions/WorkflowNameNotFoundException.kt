package de.partspicker.web.workflow.business.exceptions

class WorkflowNameNotFoundException(workflowName: String) : Exception(
    "Workflow with name $workflowName could not be found"
)
