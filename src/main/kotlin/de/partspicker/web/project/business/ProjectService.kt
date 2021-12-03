package de.partspicker.web.project.business

import de.partspicker.web.project.business.exceptions.GroupNotFoundException
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.GroupRepository
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.project.persistance.entities.ProjectEntity
import org.springframework.stereotype.Service

@Service
class ProjectService(
    private val projectRepo: ProjectRepository,
    private val groupRepository: GroupRepository
) {
    fun existsById(id: Long) = this.projectRepo.existsById(id)

    fun findAll(): Iterable<ProjectEntity> = this.projectRepo.findAll()

    fun findById(projectId: Long): ProjectEntity {
        val project = projectRepo.findById(projectId)

        if (project.isEmpty) {
            throw ProjectNotFoundException(projectId)
        }

        return project.get()
    }

    fun findAllByGroupId(groupId: Long) = this.projectRepo.findAllByGroupId(groupId)

    fun save(projectEntity: ProjectEntity): ProjectEntity {
        if (!this.groupRepository.existsById(projectEntity.group?.id!!)) {
            throw GroupNotFoundException(projectEntity.group?.id!!)
        }

        return projectRepo.save(projectEntity)
    }

    fun update(projectEntity: ProjectEntity): ProjectEntity {
        if (!this.existsById(projectEntity.id!!)) {
            throw ProjectNotFoundException(projectEntity.id!!)
        }

        return this.save(projectEntity)
    }

    fun deleteById(id: Long) {
        if (!this.existsById(id)) {
            throw ProjectNotFoundException(id)
        }

        this.projectRepo.deleteById(id)
    }

    fun removeGroupForAllById(groupId: Long) {
        val projects = this.findAllByGroupId(groupId).map { project ->
            project.copy(group = null)
        }

        this.projectRepo.saveAll(projects)
    }
}
