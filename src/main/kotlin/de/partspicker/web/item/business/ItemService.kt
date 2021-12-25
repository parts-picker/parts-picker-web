package de.partspicker.web.item.business

import de.partspicker.web.item.business.exceptions.ItemNotFoundException
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.persistance.ItemRepository
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository
) {
    fun getItems() = Item.AsList.from(this.itemRepository.findAll())

    fun getItemById(id: Long): Item {
        val itemEntity = this.itemRepository.findById(id)

        if (itemEntity.isEmpty) {
            throw ItemNotFoundException(itemId = id)
        }

        return Item.from(itemEntity.get())
    }

    fun getItemsForItemType(itemTypeId: Long) = Item.AsList.from(this.itemRepository.findAllByTypeId(itemTypeId))

    fun deleteItemsForItemType(itemTypeId: Long) = this.itemRepository.deleteAllByTypeId(itemTypeId)
}
