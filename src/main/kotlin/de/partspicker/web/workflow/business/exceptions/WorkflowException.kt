package de.partspicker.web.workflow.business.exceptions

open class WorkflowException(message: String = "A workflow error occurred - please submit a bug report") :
    Exception(message)
