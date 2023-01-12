package de.partspicker.web.project.api.requests

data class ProjectPatchRequest(
    val name: String,
    val description: String?,
    val groupId: Long?
) {
    companion object {
        val DUMMY = ProjectPatchRequest(
            "",
            null,
            null
        )
    }
}
