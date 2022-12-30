package de.partspicker.web.workflow.business.exceptions

class WorkflowNodeDuplicateException(nodeNames: Set<String>) :
    RuntimeException("Two or more nodes may not have the same name: $nodeNames")
