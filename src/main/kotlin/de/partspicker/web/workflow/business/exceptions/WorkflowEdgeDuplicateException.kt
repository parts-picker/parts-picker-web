package de.partspicker.web.workflow.business.exceptions

class WorkflowEdgeDuplicateException(edgeNames: Set<String>) :
    RuntimeException("Two or more edges may not have the same name: $edgeNames")
