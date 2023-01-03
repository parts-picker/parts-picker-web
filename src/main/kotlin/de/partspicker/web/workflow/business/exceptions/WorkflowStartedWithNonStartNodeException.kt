package de.partspicker.web.workflow.business.exceptions

class WorkflowStartedWithNonStartNodeException(workflowName: String, nodeName: String) : Exception(
    "Workflow can only be started at a start node." +
        " The node with name $nodeName is not a start node of the workflow with name $workflowName"
)
