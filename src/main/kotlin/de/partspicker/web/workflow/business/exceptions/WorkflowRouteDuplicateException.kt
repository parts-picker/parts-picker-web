package de.partspicker.web.workflow.business.exceptions

class WorkflowRouteDuplicateException(edges: Set<String>) :
    RuntimeException("Routes described by edges must be unique. Duplicated routes: $edges")
