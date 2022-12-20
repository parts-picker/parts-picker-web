package de.partspicker.web.item.business

import de.partspicker.web.item.business.exceptions.ItemNotFoundException
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.item.persistance.entities.ItemEntity
import de.partspicker.web.item.persistance.entities.enums.ItemConditionEntity
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.project.persistance.entities.ProjectEntity
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val itemTypeRepository: ItemTypeRepository,
    private val projectRepository: ProjectRepository
) {

    fun create(itemToCreate: Item): Item {
        if (!this.itemTypeRepository.existsById(itemToCreate.type.id)) {
            throw ItemTypeNotFoundException(itemToCreate.type.id)
        }

        val createdItem = this.itemRepository.save(ItemEntity.from(itemToCreate))

        return Item.from(createdItem)
    }

    fun getItems(pageable: Pageable) = Item.AsPage.from(this.itemRepository.findAll(pageable))

    fun getItemById(id: Long): Item {
        val itemEntity = this.itemRepository.findById(id)

        if (itemEntity.isEmpty) {
            throw ItemNotFoundException(itemId = id)
        }

        return Item.from(itemEntity.get())
    }

    fun getItemsForItemType(itemTypeId: Long, pageable: Pageable = Pageable.unpaged()) =
        Item.AsPage.from(this.itemRepository.findAllByTypeId(itemTypeId, pageable))

    fun update(id: Long, condition: ItemCondition, note: String?): Item {
        val itemToUpdate = this.itemRepository.findById(id).orElseThrow { ItemNotFoundException(id) }

        itemToUpdate.condition = ItemConditionEntity.from(condition)
        itemToUpdate.note = note

        return Item.from(this.itemRepository.save(itemToUpdate))
    }

    fun updateAssignedProject(id: Long, assignedProjectId: Long?): Item {
        val itemToUpdate = this.itemRepository.findById(id).orElseThrow { ItemNotFoundException(id) }

        if (assignedProjectId != null) {
            if (this.projectRepository.existsById(assignedProjectId)) {
                itemToUpdate.assignedProject = ProjectEntity(id = assignedProjectId)
            } else {
                throw ProjectNotFoundException(projectId = assignedProjectId)
            }
        } else {
            itemToUpdate.assignedProject = null
        }

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
