package de.partspicker.web.item.business

import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.ItemType
import de.partspicker.web.item.persistance.ItemTypeRepository
import org.springframework.stereotype.Service

@Service
class ItemTypeService(
    private val itemTypeRepository: ItemTypeRepository
) {
    fun getItemTypes() = ItemType.AsList.from(this.itemTypeRepository.findAll())

    fun getItemTypeById(id: Long): ItemType {
        val itemTypeEntity = this.itemTypeRepository.findById(id)

        if (itemTypeEntity.isEmpty) {
            throw ItemTypeNotFoundException(itemTypeId = id)
        }

        return ItemType.from(itemTypeEntity.get())
    }
}
