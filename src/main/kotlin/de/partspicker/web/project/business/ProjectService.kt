package de.partspicker.web.project.business

import de.partspicker.web.common.business.exceptions.CrossOrgUnitReferenceException
import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.business.rules.NodeNameEqualsRule
import de.partspicker.web.common.business.rules.or
import de.partspicker.web.common.persistence.entities.CreationInfo
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.inventory.business.RequiredItemTypeService
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.orgunit.persistence.OrgUnitRepository
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
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Suppress("LongParameterList")
@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository,
    private val workflowInteractionService: WorkflowInteractionService,
    private val itemRepository: ItemRepository,
    private val requiredItemTypeService: RequiredItemTypeService,
    private val instanceRepository: InstanceRepository,
    private val orgUnitRepository: OrgUnitRepository,
    private val orgUnitAccessService: OrgUnitAccessService
) {

    @Transactional
    fun create(orgUnitId: Long, project: CreateProject): Project {
        this.orgUnitAccessService.requireAtLeast(orgUnitId, AccessLevel.EDIT)

        val groupEntity = project.groupId?.let { groupId -> this.getGroupOfOrgUnitOrThrow(groupId, orgUnitId) }

        val sourceProjectEntity = project.sourceProjectId?.let { id ->
            projectRepository.getNullableReferenceById(id)
                ?: throw ProjectNotFoundException(projectId = project.sourceProjectId)
        }

        val instance = this.workflowInteractionService.startProjectWorkflow()

        val instanceEntity = this.instanceRepository.getReferenceById(instance.id)
        val savedProjectEntity = this.projectRepository.save(
            ProjectEntity(
                name = project.name,
                shortDescription = project.shortDescription,
                description = project.description,
                group = groupEntity,
                workflowInstance = instanceEntity,
                sourceProject = sourceProjectEntity,
                orgUnit = this.orgUnitRepository.getReferenceById(orgUnitId),
                creation = CreationInfo(this.orgUnitAccessService.currentUser(), Instant.now())
            )
        )

        return Project.from(savedProjectEntity)
    }

    /**
     * Creates a new project with the given name based on the description & short description
     * of the project with the given id. The copy belongs to the org unit of its source.
     */
    @Transactional
    fun copy(sourceProjectId: Long, name: String): Project {
        val sourceProjectEntity = this.getProjectOrThrow(sourceProjectId)
        this.orgUnitAccessService.requireAtLeast(sourceProjectEntity.orgUnit.id, AccessLevel.EDIT)

        val sourceProject = Project.from(sourceProjectEntity)
        val createProject = CreateProject(
            name = name,
            shortDescription = sourceProject.shortDescription,
            description = sourceProject.description,
            groupId = sourceProject.group?.id,
            sourceProjectId = sourceProject.id
        )
        val createdProject = this.create(sourceProjectEntity.orgUnit.id, createProject)

        // copy requiredItemTypes for copied project
        this.requiredItemTypeService.copyAllToTargetProjectByProjectId(sourceProject.id, createdProject.id)

        return createdProject
    }

    fun findAllForOrgUnit(orgUnitId: Long, pageable: Pageable = Pageable.unpaged()): Page<Project> {
        this.orgUnitAccessService.requireAtLeast(orgUnitId, AccessLevel.READ)

        return Project.AsPage.from(this.projectRepository.findAllByOrgUnitId(orgUnitId, pageable))
    }

    fun getById(id: Long): Project {
        val projectEntity = this.getProjectOrThrow(id)
        this.orgUnitAccessService.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.READ)

        return Project.from(projectEntity)
    }

    /**
     * Returns the workflow instance of the given project, requiring the given level in its org unit.
     */
    fun getInstanceIdOf(projectId: Long, requiredLevel: AccessLevel): Long {
        val projectEntity = this.getProjectOrThrow(projectId)
        this.orgUnitAccessService.requireAtLeast(projectEntity.orgUnit.id, requiredLevel)

        return projectEntity.workflowInstance.id
    }

    /**
     * Reads a project without checking access, for automated workflow actions that run without a user.
     */
    fun findByInstanceId(instanceId: Long): Project? {
        val projectEntity = this.projectRepository.findByWorkflowInstanceId(instanceId) ?: return null

        return Project.from(projectEntity)
    }

    fun update(projectId: Long, shortDescription: String?, groupId: Long?): Project {
        val projectEntity = this.getProjectOrThrow(projectId)
        this.orgUnitAccessService.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.EDIT)

        ProjectActiveRule(Project.from(projectEntity)).valid()

        projectEntity.shortDescription = shortDescription

        groupId?.let { id ->
            projectEntity.group = this.getGroupOfOrgUnitOrThrow(id, projectEntity.orgUnit.id)
        }

        val updatedProject = this.projectRepository.save(projectEntity)

        return Project.from(updatedProject)
    }

    fun updateDescription(projectId: Long, description: String?): Project {
        val projectEntity = this.getProjectOrThrow(projectId)
        this.orgUnitAccessService.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.EDIT)

        ProjectActiveRule(Project.from(projectEntity)).valid()

        projectEntity.description = description

        val updatedProject = this.projectRepository.save(projectEntity)

        return Project.from(updatedProject)
    }

    fun updateName(projectId: Long, name: String): Project {
        val projectEntity = this.getProjectOrThrow(projectId)
        this.orgUnitAccessService.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.EDIT)

        ProjectActiveRule(Project.from(projectEntity)).valid()

        projectEntity.name = name

        val updatedProject = this.projectRepository.save(projectEntity)

        return Project.from(updatedProject)
    }

    @Transactional
    fun delete(id: Long) {
        val projectEntity = this.getProjectOrThrow(id)
        this.orgUnitAccessService.requireMemberCreatorOrAtLeast(
            orgUnitId = projectEntity.orgUnit.id,
            objectCreatedById = projectEntity.creation.createdBy.id,
            requiredLevel = AccessLevel.MAINTAIN
        )

        val project = Project.from(projectEntity)
        val projectStatusRule =
            NodeNameEqualsRule(project.status, "planning") or
                NodeNameEqualsRule(project.status, "implementation")
        projectStatusRule.valid()

        // remove all assigned items/item types
        this.itemRepository.updateUnassignAllByAssignedProjectId(id)
        this.requiredItemTypeService.deleteAllByProjectId(id)

        this.projectRepository.delete(projectEntity)
    }

    /**
     * Detaches every project of the given group.
     */
    fun removeGroupForAllById(groupId: Long) {
        val projects = this.projectRepository.findAllByGroupId(groupId).map { project ->
            project.copy(group = null)
        }

        this.projectRepository.saveAll(projects)
    }

    private fun getProjectOrThrow(projectId: Long): ProjectEntity =
        this.projectRepository.findByIdOrNull(projectId) ?: throw ProjectNotFoundException(projectId = projectId)

    private fun getGroupOfOrgUnitOrThrow(groupId: Long, orgUnitId: Long): GroupEntity {
        val groupEntity = this.groupRepository.findByIdOrNull(groupId) ?: throw GroupNotFoundException(groupId)

        (groupEntity.orgUnit.id == orgUnitId) elseThrow
            CrossOrgUnitReferenceException(orgUnitId, groupEntity.orgUnit.id)

        return groupEntity
    }
}
