package de.partspicker.web.project.business

import de.partspicker.web.project.business.exceptions.GroupNotFoundException
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.business.objects.Project
import de.partspicker.web.project.persistance.GroupRepository
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.project.persistance.entities.ProjectEntity
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository
) {

    fun create(project: Project): Project {
        project.group?.let { group ->
            if (!this.groupRepository.existsById(group.id)) {
                throw GroupNotFoundException(group.id)
            }
        }

        val createdProject = this.projectRepository.save(ProjectEntity.from(project))
        return Project.from(createdProject)
    }

    fun exists(id: Long) = this.projectRepository.existsById(id)

    fun readAll(pageable: Pageable = Pageable.unpaged()) = Project.AsPage.from(this.projectRepository.findAll(pageable))

    fun read(id: Long): Project {
        val projectEntity = projectRepository.findById(id)

        if (projectEntity.isEmpty) {
            throw ProjectNotFoundException(projectId = id)
        }

        return Project.from(projectEntity.get())
    }

    fun update(projectEntity: ProjectEntity): ProjectEntity {
        if (!this.exists(projectEntity.id)) {
            throw ProjectNotFoundException(projectEntity.id)
        }

        if (!this.groupRepository.existsById(projectEntity.group?.id!!)) {
            throw GroupNotFoundException(projectEntity.group?.id!!)
        }

        return this.projectRepository.save(projectEntity)
    }

    fun deleteById(id: Long) {
        if (!this.exists(id)) {
            throw ProjectNotFoundException(id)
        }

        this.projectRepository.deleteById(id)
    }

    fun removeGroupForAllById(groupId: Long) {
        val projects = this.projectRepository.findAllByGroupId(groupId).map { project ->
            project.copy(group = null)
        }

        this.projectRepository.saveAll(projects)
    }
}
