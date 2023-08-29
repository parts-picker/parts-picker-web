package de.partspicker.web.project.api.requests

data class ProjectPostRequest(
    val name: String,
    val shortDescription: String?,
    val groupId: Long?
) {
    companion object {
        val DUMMY = ProjectPostRequest(
            name = "",
            shortDescription = null,
            groupId = null
        )
    }
}
