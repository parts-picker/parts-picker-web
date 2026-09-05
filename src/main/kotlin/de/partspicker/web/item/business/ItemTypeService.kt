package de.partspicker.web.item.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.persistence.entities.CreationInfo
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.CreateItemType
import de.partspicker.web.item.business.objects.ItemType
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.orgunit.persistence.OrgUnitRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ItemTypeService(
    private val itemTypeRepository: ItemTypeRepository,
    private val itemRepository: ItemRepository,
    private val orgUnitRepository: OrgUnitRepository,
    private val orgUnitAccessService: OrgUnitAccessService
) {

    fun create(orgUnitId: Long, itemTypeToCreate: CreateItemType): ItemType {
        this.orgUnitAccessService.requireAtLeast(orgUnitId, AccessLevel.EDIT)

        val createdItemTypeEntity = this.itemTypeRepository.save(
            ItemTypeEntity(
                name = itemTypeToCreate.name,
                description = itemTypeToCreate.description,
                orgUnit = this.orgUnitRepository.getReferenceById(orgUnitId),
                creation = CreationInfo(this.orgUnitAccessService.currentUser(), Instant.now())
            )
        )

        return ItemType.from(createdItemTypeEntity)
    }

    fun findAllForOrgUnit(orgUnitId: Long, pageable: Pageable): Page<ItemType> {
        this.orgUnitAccessService.requireAtLeast(orgUnitId, AccessLevel.READ)

        return ItemType.AsPage.from(this.itemTypeRepository.findAllByOrgUnitId(orgUnitId, pageable))
    }

    fun getById(id: Long): ItemType {
        val itemTypeEntity = this.getItemTypeOrThrow(id)
        this.orgUnitAccessService.requireAtLeast(itemTypeEntity.orgUnit.id, AccessLevel.READ)

        return ItemType.from(itemTypeEntity)
    }

    fun update(id: Long, name: String, description: String?): ItemType {
        val itemTypeEntity = this.getItemTypeOrThrow(id)
        this.orgUnitAccessService.requireAtLeast(itemTypeEntity.orgUnit.id, AccessLevel.EDIT)

        itemTypeEntity.name = name
        itemTypeEntity.description = description

        return ItemType.from(this.itemTypeRepository.save(itemTypeEntity))
    }

    /**
     * Deletes the item type together with every item of it.
     */
    @Transactional
    fun delete(id: Long): Long {
        val itemTypeEntity = this.getItemTypeOrThrow(id)
        this.orgUnitAccessService.requireMemberCreatorOrAtLeast(
            orgUnitId = itemTypeEntity.orgUnit.id,
            objectCreatedById = itemTypeEntity.creation.createdBy.id,
            requiredLevel = AccessLevel.MAINTAIN
        )

        val amountOfDeletedItems = this.itemRepository.deleteAllByTypeId(id)
        this.itemTypeRepository.delete(itemTypeEntity)

        return amountOfDeletedItems
    }

    private fun getItemTypeOrThrow(itemTypeId: Long): ItemTypeEntity =
        this.itemTypeRepository.findByIdOrNull(itemTypeId) ?: throw ItemTypeNotFoundException(itemTypeId = itemTypeId)
}
