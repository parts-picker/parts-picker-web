package de.partspicker.web.project.api.requests

data class ProjectMetaInfoPatchRequest(
    val name: String,
    val shortDescription: String?,
    val groupId: Long?
) : ProjectPatchRequest {
    companion object {
        val DUMMY = ProjectMetaInfoPatchRequest(
            "",
            null,
            null
        )
    }
}
