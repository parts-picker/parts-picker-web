package de.partspicker.web.workflow.business.automated.exceptions

class InstanceNotRelatedToProjectException(instanceId: Long) : AutomatedActionException(
    "No project found for the given instanceId $instanceId" +
        " - this suggest a workflow configuration error or a serious bug"
)
