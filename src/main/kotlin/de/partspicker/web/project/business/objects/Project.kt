package de.partspicker.web.project.business.objects

import de.partspicker.web.project.persistance.entities.ProjectEntity
import org.springframework.data.domain.Page

data class Project(
    val id: Long,
    val name: String,
    val shortDescription: String?,
    val description: String?,
    var group: Group?,
    var workflowInstanceId: Long,
    val status: String,
    val active: Boolean
) {
    companion object {
        fun from(projectEntity: ProjectEntity) = Project(
            id = projectEntity.id,
            name = projectEntity.name,
            shortDescription = projectEntity.shortDescription,
            description = projectEntity.description,
            group = projectEntity.group?.let { groupEntity -> Group.from(groupEntity) },
            workflowInstanceId = projectEntity.workflowInstance.id,
            status = projectEntity.workflowInstance.currentNode.name,
            active = projectEntity.workflowInstance.active
        )
    }

    object AsPage {
        fun from(pagedProjectEntities: Page<ProjectEntity>) = pagedProjectEntities.map { from(it) }
    }
}
