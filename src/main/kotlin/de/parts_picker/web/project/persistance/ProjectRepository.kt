package de.parts_picker.web.project.persistance

import de.parts_picker.web.project.persistance.entities.ProjectEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : JpaRepository<ProjectEntity, Long> {
    fun findAllByGroupId(groupId: Long): List<ProjectEntity>
}