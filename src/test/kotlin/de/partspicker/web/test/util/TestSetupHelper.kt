package de.partspicker.web.test.util

import de.partspicker.web.inventory.business.RequiredItemTypeService
import de.partspicker.web.inventory.business.objects.CreateOrUpdateRequiredItemType
import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.item.business.ItemService
import de.partspicker.web.item.business.ItemTypeService
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.business.objects.ItemType
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.business.objects.enums.ItemStatus
import de.partspicker.web.project.business.ProjectService
import de.partspicker.web.project.business.objects.CreateProject
import de.partspicker.web.project.business.objects.Project
import jakarta.persistence.EntityManager
import org.hibernate.search.mapper.orm.Search
import org.springframework.stereotype.Component

/**
 * Util class which bundles utilities to set up the testing environment.
 */
@Component
class TestSetupHelper(
    private val entityManager: EntityManager,
    private val projectService: ProjectService,
    private val requiredItemTypeService: RequiredItemTypeService,
    private val itemTypeService: ItemTypeService,
    private val itemService: ItemService
) {
    /**
     * Useful if flush & clear is needed for changes to be visible for search or cache refresh.
     */
    fun flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }

    fun manualSearchIndexing() {
        entityManager.flush()
        val searchSession = Search.session(entityManager)
        searchSession.indexingPlan().execute()
    }

    fun manualSearchClearing() {
        val searchSession = Search.session(entityManager)
        searchSession.workspace().purge()
    }

    fun setupProject(): Project {
        val project = projectService.create(
            CreateProject(
                "Test Project",
                ""
            )
        )
        this.flushAndClear()

        return project
    }

    fun setupItemTypes(
        amountToCreate: Int = 1,
        name: String = "itemType",
    ): List<ItemType> {
        val itemTypes = mutableListOf<ItemType>()

        repeat(amountToCreate) {
            itemTypes.add(this.setupItemType(name))
        }

        return itemTypes
    }

    fun setupItemType(name: String = "itemType"): ItemType {
        return itemTypeService.create(
            ItemType(id = 0, name = name, description = "")
        )
    }

    fun setupRequiredItemType(projectId: Long, itemTypeId: Long, requiredAmount: Long = 1): RequiredItemType {
        return requiredItemTypeService.createOrUpdate(
            CreateOrUpdateRequiredItemType(
                projectId = projectId,
                itemTypeId = itemTypeId,
                requiredAmount = requiredAmount
            )
        )
    }

    fun setupItemsForType(
        amountToCreate: Int = 1,
        itemType: ItemType,
        projectId: Long? = null,
        status: ItemStatus? = null,
        condition: ItemCondition = ItemCondition.NEW
    ): List<Item> {
        val items = mutableListOf<Item>()

        repeat(amountToCreate) {
            items.add(
                setupItemForType(
                    itemType = itemType,
                    projectId = projectId,
                    status = status,
                    condition = condition
                )
            )
        }

        return items
    }

    fun setupItemForType(
        itemType: ItemType,
        projectId: Long? = null,
        status: ItemStatus? = null,
        condition: ItemCondition = ItemCondition.NEW
    ): Item {
        val item = itemService.create(
            Item(
                id = 0,
                type = itemType,
                assignedProjectId = projectId,
                status = status ?: if (projectId != null) ItemStatus.RESERVED else ItemStatus.IN_STOCK,
                condition = condition,
                note = null
            )
        )

        this.flushAndClear()

        return item
    }
}
