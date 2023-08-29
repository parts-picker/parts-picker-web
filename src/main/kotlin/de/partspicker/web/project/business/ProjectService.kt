package de.partspicker.web.project.business

import de.partspicker.web.project.business.exceptions.GroupNotFoundException
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.business.objects.CreateProject
import de.partspicker.web.project.business.objects.Project
import de.partspicker.web.project.persistance.GroupRepository
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.project.persistance.entities.GroupEntity
import de.partspicker.web.project.persistance.entities.ProjectEntity
import de.partspicker.web.workflow.business.WorkflowInteractionService
import de.partspicker.web.workflow.persistence.InstanceRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository,
    private val workflowInteractionService: WorkflowInteractionService,
    private val instanceRepository: InstanceRepository
) {

    @Transactional
    fun create(project: CreateProject): Project {
        project.groupId?.let { groupId ->
            if (!this.groupRepository.existsById(groupId)) {
                throw GroupNotFoundException(groupId)
            }
        }

        val instance = this.workflowInteractionService.startProjectWorkflow()

        val instanceEntity = this.instanceRepository.getReferenceById(instance.id)
        val savedProjectEntity = this.projectRepository.save(ProjectEntity.from(project, instanceEntity))

        return Project.from(savedProjectEntity)
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

    fun readByInstanceId(instanceId: Long): Project? {
        val projectEntity = this.projectRepository.findByWorkflowInstanceId(instanceId) ?: return null

        return Project.from(projectEntity)
    }

    fun update(projectId: Long, name: String, shortDescription: String?, groupId: Long?): Project {
        val projectEntity = this.projectRepository.findById(projectId).orElseThrow {
            throw ProjectNotFoundException(projectId)
        }

        projectEntity.name = name
        projectEntity.shortDescription = shortDescription

        groupId?.let { id ->
            if (!this.groupRepository.existsById(id)) {
                throw GroupNotFoundException(id)
            }

            projectEntity.group = GroupEntity(id = id)
        }

        val updatedProject = this.projectRepository.save(projectEntity)

        return Project.from(updatedProject)
    }

    fun delete(id: Long) {
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
