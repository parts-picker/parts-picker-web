package de.partspicker.web.project.business.objects

import de.partspicker.web.project.api.requests.ProjectPostRequest
import de.partspicker.web.project.api.requests.ProjectPutRequest
import de.partspicker.web.project.persistance.entities.ProjectEntity
import org.springframework.data.domain.Page

data class Project(
    val id: Long,
    val name: String,
    val description: String?,
    var group: Group?
) {
    companion object {
        fun from(projectEntity: ProjectEntity) = Project(
            id = projectEntity.id,
            name = projectEntity.name,
            description = projectEntity.description,
            group = projectEntity.group?.let { groupEntity -> Group.from(groupEntity) }
        )

        fun from(projectPostRequest: ProjectPostRequest) = Project(
            id = 0,
            name = projectPostRequest.name,
            description = projectPostRequest.description,
            group = projectPostRequest.groupId?.let { groupId ->
                if (groupId != 0L) Group(id = groupId) else null
            }
        )

        fun from(projectPutRequest: ProjectPutRequest, id: Long) = Project(
            id = id,
            name = projectPutRequest.name,
            description = projectPutRequest.description,
            group = projectPutRequest.groupId?.let { groupId -> Group(id = groupId) }
        )
    }

    object AsPage {
        fun from(pagedProjectEntities: Page<ProjectEntity>) = pagedProjectEntities.map { from(it) }
    }
}
