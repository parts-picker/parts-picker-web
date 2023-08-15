package de.partspicker.web.workflow.business.exceptions

class WorkflowLatestVersionIsGreaterException(val name: String, val latestVersion: Long, val requestedVersion: Long) :
    RuntimeException(
        "The latest version V$latestVersion of the workflow '$name' is " +
            "greater than the requested version V$requestedVersion for creation"
    )
