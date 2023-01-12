package de.partspicker.web.project.business.objects

import de.partspicker.web.project.api.requests.ProjectPostRequest

data class CreateProject(
    val id: Long,
    val name: String? = null,
    val description: String? = null,
    var groupId: Long? = null
) {
    companion object {
        fun from(projectPostRequest: ProjectPostRequest) = CreateProject(
            id = 0,
            name = projectPostRequest.name,
            description = projectPostRequest.description,
            groupId = projectPostRequest.groupId
        )
    }
}
