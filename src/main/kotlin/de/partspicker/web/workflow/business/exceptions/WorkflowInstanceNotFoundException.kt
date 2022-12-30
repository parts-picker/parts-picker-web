package de.partspicker.web.workflow.business.exceptions

class WorkflowInstanceNotFoundException(instanceId: Long) : Exception(
    "Workflow instance with id $instanceId could not be found"
)
