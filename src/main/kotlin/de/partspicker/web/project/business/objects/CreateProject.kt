package de.partspicker.web.project.business.objects

import de.partspicker.web.project.api.requests.ProjectPostRequest

data class CreateProject(
    val name: String,
    val shortDescription: String? = null,
    val description: String? = null,
    val groupId: Long? = null,
    val sourceProjectId: Long? = null
) {
    companion object {
        fun from(projectPostRequest: ProjectPostRequest) = CreateProject(
            name = projectPostRequest.name,
            shortDescription = projectPostRequest.shortDescription,
            description = null,
            groupId = projectPostRequest.groupId,
            sourceProjectId = null
        )
    }
}
