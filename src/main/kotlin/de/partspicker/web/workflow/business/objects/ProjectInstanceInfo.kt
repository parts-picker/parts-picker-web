package de.partspicker.web.workflow.business.objects

import de.partspicker.web.project.persistance.entities.ProjectEntity

/**
 * The [InstanceInfo] of a project's workflow, together with the project it belongs to.
 */
data class ProjectInstanceInfo(
    val projectId: Long,
    val orgUnitId: Long,
    val instanceInfo: InstanceInfo
) {
    companion object {
        fun from(projectEntity: ProjectEntity, instanceInfo: InstanceInfo) = ProjectInstanceInfo(
            projectId = projectEntity.id,
            orgUnitId = projectEntity.orgUnit.id,
            instanceInfo = instanceInfo
        )
    }
}
