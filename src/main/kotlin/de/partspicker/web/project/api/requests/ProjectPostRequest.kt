package de.partspicker.web.project.api.requests

data class ProjectPostRequest(
    val name: String,
    val description: String?,
    val groupId: Long?
) {
    companion object {
        val DUMMY = ProjectPostRequest(
            name = "",
            description = null,
            groupId = null
        )
    }
}
