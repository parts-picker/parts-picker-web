package de.partspicker.web.item.business

import de.partspicker.web.item.business.exceptions.ItemNotFoundException
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.business.objects.enums.ItemStatus
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.item.persistance.entities.ItemEntity
import de.partspicker.web.item.persistance.entities.enums.ItemConditionEntity
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val itemTypeRepository: ItemTypeRepository
) {

    fun create(itemToCreate: Item): Item {
        if (!this.itemTypeRepository.existsById(itemToCreate.type.id)) {
            throw ItemTypeNotFoundException(itemToCreate.type.id)
        }

        val createdItem = this.itemRepository.save(ItemEntity.from(itemToCreate))

        return Item.from(createdItem)
    }

    fun getItems() = Item.AsList.from(this.itemRepository.findAll())

    fun getItemById(id: Long): Item {
        val itemEntity = this.itemRepository.findById(id)

        if (itemEntity.isEmpty) {
            throw ItemNotFoundException(itemId = id)
        }

        return Item.from(itemEntity.get())
    }

    fun getItemsForItemType(itemTypeId: Long) = Item.AsList.from(this.itemRepository.findAllByTypeId(itemTypeId))

    fun update(id: Long, status: ItemStatus, condition: ItemCondition, note: String? = null): Item {
        if (!this.itemRepository.existsById(id)) {
            throw ItemNotFoundException(itemId = id)
        }

        val itemToUpdate = this.itemRepository.getById(id)
        itemToUpdate.status = ItemStatusEntity.from(status)
        itemToUpdate.condition = ItemConditionEntity.from(condition)
        itemToUpdate.note = note

        return Item.from(this.itemRepository.save(itemToUpdate))
    }

    fun delete(id: Long) {
        if (!this.itemRepository.existsById(id)) {
            throw ItemNotFoundException(id)
        }

        this.itemRepository.deleteById(id)
    }

    fun deleteItemsForItemType(itemTypeId: Long) = this.itemRepository.deleteAllByTypeId(itemTypeId)
}
