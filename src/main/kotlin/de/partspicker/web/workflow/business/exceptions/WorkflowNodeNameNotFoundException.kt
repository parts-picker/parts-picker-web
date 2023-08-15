package de.partspicker.web.workflow.business.exceptions

import de.partspicker.web.common.exceptions.EntityNotFoundException

class WorkflowNodeNameNotFoundException(workflowName: String, nodeName: String) : EntityNotFoundException(
    "Workflow node with name $nodeName could not be found for workflow with name $workflowName"
)
