package de.partspicker.web.workflow.business.exceptions

import de.partspicker.web.common.exceptions.EntityNotFoundException

class WorkflowInstanceNotFoundException(instanceId: Long) :
    EntityNotFoundException("Workflow instance with id $instanceId could not be found")
