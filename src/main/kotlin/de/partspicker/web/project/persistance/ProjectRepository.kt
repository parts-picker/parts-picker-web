package de.partspicker.web.project.persistance

import de.partspicker.web.project.persistance.entities.ProjectEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : JpaRepository<ProjectEntity, Long> {
    fun findAllByGroupId(groupId: Long): List<ProjectEntity>
    fun findByWorkflowInstanceId(instanceId: Long): ProjectEntity?
}
