package de.partspicker.web.workflow.business.exceptions

class WorkflowAlreadyExistsException(name: String, version: Long) :
    RuntimeException("Workflow with name '$name' and version '$version' already exists")
