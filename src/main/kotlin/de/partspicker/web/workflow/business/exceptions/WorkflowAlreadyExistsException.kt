package de.partspicker.web.workflow.business.exceptions

class WorkflowAlreadyExistsException(val name: String, val version: Long) :
    RuntimeException("Workflow with name '$name' and version '$version' already exists")
