package de.partspicker.web.workflow.api.exceptions

class WorkflowStartupReadException(message: String) : RuntimeException(message) {
    companion object {
        fun from(
            name: String,
            version: Long,
            sourcePath: String,
            cause: Throwable
        ): WorkflowStartupReadException {
            return WorkflowStartupReadException(
                "Workflow with name '$name', version $version from file '$sourcePath' is invalid: ${cause.message}"
            )
        }
    }
}
