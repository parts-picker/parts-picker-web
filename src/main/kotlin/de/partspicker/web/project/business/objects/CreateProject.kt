package de.partspicker.web.project.business.objects

import de.partspicker.web.project.api.requests.ProjectPostRequest

data class CreateProject(
    val name: String,
    val shortDescription: String? = null,
    var groupId: Long? = null
) {
    companion object {
        fun from(projectPostRequest: ProjectPostRequest) = CreateProject(
            name = projectPostRequest.name,
            shortDescription = projectPostRequest.shortDescription,
            groupId = projectPostRequest.groupId
        )
    }
}
