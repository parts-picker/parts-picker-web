package de.partspicker.web.project.api.requests

data class GroupPutRequest(
    val name: String,
    val description: String?
) {
    companion object {
        val DUMMY = GroupPutRequest(name = "", description = null)
    }
}
