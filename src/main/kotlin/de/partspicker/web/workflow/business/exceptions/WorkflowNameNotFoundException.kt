package de.partspicker.web.workflow.business.exceptions

import de.partspicker.web.common.exceptions.EntityNotFoundException

class WorkflowNameNotFoundException(workflowName: String) :
    EntityNotFoundException("Workflow with name $workflowName could not be found")
