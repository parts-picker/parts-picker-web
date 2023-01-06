package de.partspicker.web.workflow.api.requests

data class AdvanceInstanceStateRequest(
    val values: Map<String, Any>
) {
    companion object {
        val DUMMY = AdvanceInstanceStateRequest(emptyMap())
    }
}
