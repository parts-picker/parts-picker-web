package de.partspicker.web.workflow.business.exceptions

class InstanceNotFoundException(instanceId: Long) : Exception(
    "Workflow instance with id $instanceId could not be found"
)
