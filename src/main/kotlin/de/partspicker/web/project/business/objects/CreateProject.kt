package de.partspicker.web.project.business.objects

import de.partspicker.web.project.api.requests.ProjectPostRequest

data class CreateProject(
    val name: String,
    val description: String? = null,
    var groupId: Long? = null
) {
    companion object {
        fun from(projectPostRequest: ProjectPostRequest) = CreateProject(
            name = projectPostRequest.name,
            description = projectPostRequest.description,
            groupId = projectPostRequest.groupId
        )
    }
}
