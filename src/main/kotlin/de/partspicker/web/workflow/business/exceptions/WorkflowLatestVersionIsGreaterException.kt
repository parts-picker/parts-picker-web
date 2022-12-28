package de.partspicker.web.workflow.business.exceptions

class WorkflowLatestVersionIsGreaterException(name: String, latestVersion: Long, requestedVersion: Long) :
    RuntimeException(
        "The latest version '$latestVersion' of the workflow '$name' is " +
            "greater than the requested version '$requestedVersion'"
    )
