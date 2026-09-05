package de.partspicker.web.project.api.requests

data class GroupPostRequest(
    val name: String,
    val description: String?
) {
    companion object {
        val DUMMY = GroupPostRequest(name = "", description = null)
    }
}
