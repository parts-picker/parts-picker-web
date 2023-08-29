package de.partspicker.web.project.business.objects

import de.partspicker.web.project.persistance.entities.ProjectEntity
import org.springframework.data.domain.Page

data class Project(
    val id: Long,
    val name: String,
    val shortDescription: String? = null,
    var group: Group? = null,
    var workflowInstanceId: Long,
    val status: String
) {
    companion object {
        fun from(projectEntity: ProjectEntity) = Project(
            id = projectEntity.id,
            name = projectEntity.name,
            shortDescription = projectEntity.shortDescription,
            group = projectEntity.group?.let { groupEntity -> Group.from(groupEntity) },
            workflowInstanceId = projectEntity.workflowInstance.id,
            status = projectEntity.workflowInstance.currentNode.name
        )
    }

    object AsPage {
        fun from(pagedProjectEntities: Page<ProjectEntity>) = pagedProjectEntities.map { from(it) }
    }
}
