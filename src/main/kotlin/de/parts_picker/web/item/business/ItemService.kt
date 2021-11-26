package de.parts_picker.web.item.business

import de.parts_picker.web.item.business.exceptions.ItemNotFoundException
import de.parts_picker.web.item.business.objects.Item
import de.parts_picker.web.item.persistance.ItemRepository
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository
) {
    fun getItems() = Item.List.from(this.itemRepository.findAll())

    fun getItemById(id: Long): Item {
        val itemEntity = this.itemRepository.findById(id)

        if (itemEntity.isEmpty) {
            throw ItemNotFoundException(itemId = id)
        }

        return Item.from(itemEntity.get())
    }
}