package de.partspicker.web.project.api.requests

data class ProjectMetaInfoPatchRequest(
    val shortDescription: String?,
    val groupId: Long?
) : ProjectPatchRequest {
    companion object {
        val DUMMY = ProjectMetaInfoPatchRequest(
            null,
            null
        )
    }
}
