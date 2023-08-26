package de.partspicker.web.workflow.business.exceptions

class ProjectWorkflowInstanceHasNoProjectException(instanceId: Long) : WorkflowException(
    "The instance with the given id $instanceId is not assigned to a project but its " +
        "workflow is 'project_workflow'"
)
