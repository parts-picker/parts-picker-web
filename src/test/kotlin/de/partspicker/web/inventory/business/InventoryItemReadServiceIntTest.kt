package de.partspicker.web.inventory.business

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
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("integration")
@Transactional
class InventoryItemReadServiceIntTest(
    private val cut: InventoryItemReadService,
    // support classes
    private val projectService: ProjectService,
    private val itemTypeService: ItemTypeService,
    private val itemService: ItemService,
    private val requiredItemTypeService: RequiredItemTypeService,
    private val entityManager: EntityManager
) : ShouldSpec({
    // helpers
    fun flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }

    fun setupProject(): Project {
        val project = projectService.create(
            CreateProject(
                "Test Project",
                ""
            )
        )
        flushAndClear()

        return project
    }

    fun setupItemType(name: String = "itemType"): ItemType {
        return itemTypeService.create(
            ItemType(id = 0, name = name, description = "")
        )
    }

    fun setupItemForType(itemType: ItemType, projectId: Long? = null): Item {
        return itemService.create(
            Item(
                id = 0,
                type = itemType,
                assignedProjectId = projectId,
                status = if (projectId == null) ItemStatus.IN_STOCK else ItemStatus.RESERVED,
                condition = ItemCondition.NEW,
                note = null
            )
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

    context("countAssignedForItemTypeAndProject") {
        should("return correct number of items for given type and project") {
            // given
            val project = setupProject()
            val itemType = setupItemType()

            val itemAmount = 15
            setupRequiredItemType(project.id, itemType.id, itemAmount.toLong())

            repeat(itemAmount) {
                setupItemForType(itemType, project.id)
            }
            // create some items not assigned to the given project as well
            repeat(5) {
                setupItemForType(itemType)
            }

            // when
            val count = cut.countAssignedForItemTypeAndProject(itemTypeId = itemType.id, projectId = project.id)

            // then
            count shouldBe itemAmount
        }

        should("return zero when given project without items assigned") {
            // given
            val project = setupProject()
            val itemType = setupItemType()

            // create some items not assigned to the given project as well
            repeat(5) {
                setupItemForType(itemType)
            }

            // when
            val count = cut.countAssignedForItemTypeAndProject(itemTypeId = itemType.id, projectId = project.id)

            // then
            count shouldBe 0
        }
    }
})
