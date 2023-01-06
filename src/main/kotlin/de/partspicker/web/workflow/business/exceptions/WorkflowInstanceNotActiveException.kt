package de.partspicker.web.workflow.business.exceptions

class WorkflowInstanceNotActiveException(instanceId: Long) : RuntimeException(
    "Workflow instance with id $instanceId cannot be edited because it is inactive."
)
