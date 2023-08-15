package de.partspicker.web.workflow.business.exceptions

import de.partspicker.web.common.exceptions.EntityNotFoundException

class WorkflowEdgeNotFoundException(edgeId: Long) :
    EntityNotFoundException("Workflow edge with id $edgeId could not be found")
