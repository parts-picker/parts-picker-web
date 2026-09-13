package de.partspicker.web.project.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.persistence.entities.CreationInfoFactory
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.orgunit.persistence.OrgUnitRepository
import de.partspicker.web.project.business.exceptions.GroupNotFoundException
import de.partspicker.web.project.business.objects.Group
import de.partspicker.web.project.persistance.GroupRepository
import de.partspicker.web.project.persistance.entities.GroupEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val projectService: ProjectService,
    private val orgUnitRepository: OrgUnitRepository,
    private val orgUnitAccessService: OrgUnitAccessService,
    private val creationInfoFactory: CreationInfoFactory
) {

    fun getById(id: Long): Group {
        val groupEntity = this.getGroupOrThrow(id)
        this.orgUnitAccessService.requireAtLeast(groupEntity.orgUnit.id, AccessLevel.READ)

        return Group.from(groupEntity)
    }

    fun findAllForOrgUnit(orgUnitId: Long, pageable: Pageable): Page<Group> {
        this.orgUnitAccessService.requireAtLeast(orgUnitId, AccessLevel.READ)

        return this.groupRepository.findAllByOrgUnitId(orgUnitId, pageable).map { Group.from(it) }
    }

    fun create(orgUnitId: Long, name: String, description: String?): Group {
        this.orgUnitAccessService.requireAtLeast(orgUnitId, AccessLevel.CONFIGURE)

        return Group.from(
            this.groupRepository.save(
                GroupEntity(
                    name = name,
                    description = description,
                    orgUnit = this.orgUnitRepository.getReferenceById(orgUnitId),
                    creation = this.creationInfoFactory.forCurrentUser()
                )
            )
        )
    }

    fun update(id: Long, name: String, description: String?): Group {
        val groupEntity = this.getGroupOrThrow(id)
        this.orgUnitAccessService.requireAtLeast(groupEntity.orgUnit.id, AccessLevel.CONFIGURE)

        groupEntity.name = name
        groupEntity.description = description

        return Group.from(this.groupRepository.save(groupEntity))
    }

    fun delete(id: Long) {
        val groupEntity = this.getGroupOrThrow(id)
        this.orgUnitAccessService.requireMemberCreatorOrAtLeast(
            orgUnitId = groupEntity.orgUnit.id,
            objectCreatedById = groupEntity.creation.createdBy.id,
            requiredLevel = AccessLevel.MAINTAIN
        )

        this.projectService.removeGroupForAllById(id)
        this.groupRepository.delete(groupEntity)
    }

    private fun getGroupOrThrow(groupId: Long): GroupEntity =
        this.groupRepository.findByIdOrNull(groupId) ?: throw GroupNotFoundException(groupId)
}
