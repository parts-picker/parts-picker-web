package de.partspicker.web.project.business

import de.partspicker.web.common.business.rules.NodeNameEqualsRule
import de.partspicker.web.common.business.rules.or
import de.partspicker.web.inventory.business.RequiredItemTypeService
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.project.business.exceptions.GroupNotFoundException
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.business.objects.CreateProject
import de.partspicker.web.project.business.objects.Project
import de.partspicker.web.project.business.rules.ProjectActiveRule
import de.partspicker.web.project.persistance.GroupRepository
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.project.persistance.entities.GroupEntity
import de.partspicker.web.project.persistance.entities.ProjectEntity
import de.partspicker.web.workflow.business.WorkflowInteractionService
import de.partspicker.web.workflow.persistence.InstanceRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository,
    private val workflowInteractionService: WorkflowInteractionService,
    private val itemRepository: ItemRepository,
    private val requiredItemTypeService: RequiredItemTypeService,
    private val instanceRepository: InstanceRepository
) {

    @Transactional
    fun create(project: CreateProject): Project {
        project.groupId?.let { groupId ->
            if (!this.groupRepository.existsById(groupId)) {
                throw GroupNotFoundException(groupId)
            }
        }

        val sourceProjectEntity = project.sourceProjectId?.let { id ->
            projectRepository.getNullableReferenceById(id)
                ?: throw ProjectNotFoundException(projectId = project.sourceProjectId)
        }

        val instance = this.workflowInteractionService.startProjectWorkflow()

        val instanceEntity = this.instanceRepository.getReferenceById(instance.id)
        val savedProjectEntity = this.projectRepository.save(
            ProjectEntity.from(project, instanceEntity, sourceProjectEntity)
        )

        return Project.from(savedProjectEntity)
    }

    /**
     * Creates a new project with the given name based on the description & short description
     * of the project with the given id.
     */
    @Transactional
    fun copy(sourceProjectId: Long, name: String): Project {
        val sourceProject = this.read(sourceProjectId)

        val createProject = CreateProject(
            name = name,
            shortDescription = sourceProject.shortDescription,
            description = sourceProject.description,
            groupId = sourceProject.group?.id,
            sourceProjectId = sourceProject.id
        )
        val createdProject = this.create(createProject)

        // copy requiredItemTypes for copied project
        this.requiredItemTypeService.copyAllToTargetProjectByProjectId(sourceProject.id, createdProject.id)

        return createdProject
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

    fun update(projectId: Long, shortDescription: String?, groupId: Long?): Project {
        val projectEntity = this.projectRepository.findById(projectId).orElseThrow {
            throw ProjectNotFoundException(projectId)
        }

        ProjectActiveRule(Project.from(projectEntity)).valid()

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

    fun updateDescription(projectId: Long, description: String?): Project {
        val projectEntity = this.projectRepository.getNullableReferenceById(projectId)
            ?: throw ProjectNotFoundException(projectId)

        ProjectActiveRule(Project.from(projectEntity)).valid()

        projectEntity.description = description

        val updatedProject = this.projectRepository.save(projectEntity)

        return Project.from(updatedProject)
    }

    fun updateName(projectId: Long, name: String): Project {
        val projectEntity = this.projectRepository.getNullableReferenceById(projectId)
            ?: throw ProjectNotFoundException(projectId)

        ProjectActiveRule(Project.from(projectEntity)).valid()

        projectEntity.name = name

        val updatedProject = this.projectRepository.save(projectEntity)

        return Project.from(updatedProject)
    }

    @Transactional
    fun delete(id: Long) {
        val project = this.read(id)

        val projectStatusRule =
            NodeNameEqualsRule(project.status, "planning") or
                NodeNameEqualsRule(project.status, "implementation")
        projectStatusRule.valid()

        // remove all assigned items/item types
        this.itemRepository.updateUnassignAllByAssignedProjectId(id)
        this.requiredItemTypeService.deleteAllByProjectId(id)

        this.projectRepository.deleteById(id)
    }

    fun removeGroupForAllById(groupId: Long) {
        val projects = this.projectRepository.findAllByGroupId(groupId).map { project ->
            project.copy(group = null)
        }

        this.projectRepository.saveAll(projects)
    }
}
