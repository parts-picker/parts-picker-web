package de.partspicker.web.item.business

import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.ItemType
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemTypeService(
    private val itemTypeRepository: ItemTypeRepository,
    private val itemService: ItemService
) {

    fun create(itemTypeToCreate: ItemType): ItemType {
        val createdItemType = this.itemTypeRepository.save(ItemTypeEntity.from(itemTypeToCreate))

        return ItemType.from(createdItemType)
    }

    fun getItemTypes(pageable: Pageable) = ItemType.AsPage.from(this.itemTypeRepository.findAll(pageable))

    fun getItemTypeById(id: Long): ItemType {
        val itemTypeEntity = this.itemTypeRepository.findById(id)

        if (itemTypeEntity.isEmpty) {
            throw ItemTypeNotFoundException(itemTypeId = id)
        }

        return ItemType.from(itemTypeEntity.get())
    }

    fun update(itemTypeToUpdate: ItemType): ItemType {
        if (!this.itemTypeRepository.existsById(itemTypeToUpdate.id)) {
            throw ItemTypeNotFoundException(itemTypeId = itemTypeToUpdate.id)
        }

        val updatedItemType = this.itemTypeRepository.save(ItemTypeEntity.from(itemTypeToUpdate))

        return ItemType.from(updatedItemType)
    }

    @Transactional
    fun deleteItemTypeById(id: Long): Long {
        if (!this.itemTypeRepository.existsById(id)) {
            throw ItemTypeNotFoundException(itemTypeId = id)
        }

        val amountOfDeletedItems = this.itemService.deleteItemsForItemType(id)
        this.itemTypeRepository.deleteById(id)

        return amountOfDeletedItems
    }
}
