package de.partspicker.web.project.business.objects

import de.partspicker.web.project.persistance.entities.ProjectEntity
import org.springframework.data.domain.Page

data class Project(
    val id: Long,
    val name: String,
    val description: String?,
    var groupId: Long?
) {
    companion object {
        fun from(projectEntity: ProjectEntity) = Project(
            id = projectEntity.id,
            name = projectEntity.name,
            description = projectEntity.description,
            groupId = projectEntity.group?.id
        )
    }

    object AsPage {
        fun from(pagedProjectEntities: Page<ProjectEntity>) = pagedProjectEntities.map { from(it) }
    }
}
