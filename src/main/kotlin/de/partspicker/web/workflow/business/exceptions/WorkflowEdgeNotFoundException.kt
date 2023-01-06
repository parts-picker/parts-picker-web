package de.partspicker.web.workflow.business.exceptions

class WorkflowEdgeNotFoundException(edgeId: Long) : RuntimeException(
    "Workflow edge with id $edgeId could not be found"
)
