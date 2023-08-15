package de.partspicker.web.workflow.business.exceptions

class WorkflowNotFoundException(workflowId: Long) :
    RuntimeException("Workflow with id '$workflowId' could not be found")
