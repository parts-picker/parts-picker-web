package de.partspicker.web.workflow.business.exceptions

class WorkflowNodeNameNotFoundException(workflowName: String, nodeName: String) : Exception(
    "Workflow node with name $nodeName could not be found for workflow with name $workflowName"
)
