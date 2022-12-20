package de.partspicker.web.project.api.requests

data class ProjectPutRequest(
    val name: String,
    val description: String?,
    val groupId: Long?
) {
    companion object {
        val DUMMY = ProjectPutRequest(
            "",
            null,
            null
        )
    }
}
