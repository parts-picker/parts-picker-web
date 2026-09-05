package de.partspicker.web.item.business

import de.partspicker.web.common.business.exceptions.CrossOrgUnitReferenceException
import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.persistence.entities.CreationInfo
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.item.business.exceptions.ItemNotFoundException
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.CreateItem
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.item.persistance.entities.ItemEntity
import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import de.partspicker.web.item.persistance.entities.enums.ItemConditionEntity
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val itemTypeRepository: ItemTypeRepository,
    private val projectRepository: ProjectRepository,
    private val orgUnitAccessService: OrgUnitAccessService
) {

    /**
     * Creates an item within the org unit of the item type it is created for.
     */
    fun create(itemToCreate: CreateItem): Item {
        val itemTypeEntity = this.getItemTypeOrThrow(itemToCreate.itemTypeId)
        val orgUnitEntity = itemTypeEntity.orgUnit
        this.orgUnitAccessService.requireAtLeast(orgUnitEntity.id, AccessLevel.EDIT)

        val projectEntity = itemToCreate.assignedProjectId?.let { projectId ->
            val project = this.projectRepository.getNullableReferenceById(projectId)
                ?: throw ProjectNotFoundException(projectId)

            (project.orgUnit.id == orgUnitEntity.id) elseThrow
                CrossOrgUnitReferenceException(orgUnitEntity.id, project.orgUnit.id)

            project
        }

        val createdItemEntity = this.itemRepository.save(
            ItemEntity(
                type = itemTypeEntity,
                assignedProject = projectEntity,
                status = ItemStatusEntity.from(itemToCreate.status),
                condition = ItemConditionEntity.from(itemToCreate.condition),
                note = itemToCreate.note,
                orgUnit = orgUnitEntity,
                creation = CreationInfo(this.orgUnitAccessService.currentUser(), Instant.now())
            )
        )

        return Item.from(createdItemEntity)
    }

    fun findAllForOrgUnit(orgUnitId: Long, pageable: Pageable): Page<Item> {
        this.orgUnitAccessService.requireAtLeast(orgUnitId, AccessLevel.READ)

        return Item.AsPage.from(this.itemRepository.findAllByOrgUnitId(orgUnitId, pageable))
    }

    fun getById(id: Long): Item {
        val itemEntity = this.getItemOrThrow(id)
        this.orgUnitAccessService.requireAtLeast(itemEntity.orgUnit.id, AccessLevel.READ)

        return Item.from(itemEntity)
    }

    fun findAllForItemType(itemTypeId: Long, pageable: Pageable = Pageable.unpaged()): Page<Item> {
        val itemTypeEntity = this.getItemTypeOrThrow(itemTypeId)
        this.orgUnitAccessService.requireAtLeast(itemTypeEntity.orgUnit.id, AccessLevel.READ)

        return Item.AsPage.from(this.itemRepository.findAllByTypeId(itemTypeId, pageable))
    }

    fun update(id: Long, condition: ItemCondition, note: String?): Item {
        val itemToUpdate = this.getItemOrThrow(id)
        this.orgUnitAccessService.requireAtLeast(itemToUpdate.orgUnit.id, AccessLevel.EDIT)

        itemToUpdate.condition = ItemConditionEntity.from(condition)
        itemToUpdate.note = note

        return Item.from(this.itemRepository.save(itemToUpdate))
    }

    fun delete(id: Long) {
        val itemEntity = this.getItemOrThrow(id)
        this.orgUnitAccessService.requireMemberCreatorOrAtLeast(
            orgUnitId = itemEntity.orgUnit.id,
            objectCreatedById = itemEntity.creation.createdBy.id,
            requiredLevel = AccessLevel.MAINTAIN
        )

        this.itemRepository.delete(itemEntity)
    }

    private fun getItemOrThrow(itemId: Long): ItemEntity =
        this.itemRepository.findByIdOrNull(itemId) ?: throw ItemNotFoundException(itemId = itemId)

    private fun getItemTypeOrThrow(itemTypeId: Long): ItemTypeEntity =
        this.itemTypeRepository.findByIdOrNull(itemTypeId) ?: throw ItemTypeNotFoundException(itemTypeId)
}
