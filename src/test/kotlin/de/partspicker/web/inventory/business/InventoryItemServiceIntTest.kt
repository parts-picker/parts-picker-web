package de.partspicker.web.inventory.business

import de.partspicker.web.common.business.exceptions.WrongNodeNameRuleException
import de.partspicker.web.inventory.business.exceptions.AssignableItemMustBeInStockRuleException
import de.partspicker.web.inventory.business.exceptions.ItemAlreadyAssignedRuleException
import de.partspicker.web.inventory.business.exceptions.RequiredAmountEqualOrSmallerThanAssignedAmountRuleException
import de.partspicker.web.inventory.business.objects.enums.InventoryItemCondition
import de.partspicker.web.item.business.ItemService
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.business.objects.enums.ItemStatus
import de.partspicker.web.test.util.TestSetupHelper
import de.partspicker.web.workflow.business.WorkflowInteractionService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("integration")
@Transactional
class InventoryItemServiceIntTest(
    private val cut: InventoryItemService,
    // helpers
    private val itemService: ItemService,
    private val workflowInteractionService: WorkflowInteractionService,
    private val testSetupHelper: TestSetupHelper
) : ShouldSpec({
    context("readAllAssignableForItemTypeAndProject") {
        should("return only items of the given type that are assignable to the given project") {
            // given
            val project = testSetupHelper.setupProject()
            val otherProject = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()

            val alreadyAssignedAmount = 2
            testSetupHelper.setupRequiredItemType(
                projectId = project.id,
                itemTypeId = itemType.id,
                alreadyAssignedAmount + 1L
            )
            testSetupHelper.setupItemsForType(
                amountToCreate = alreadyAssignedAmount,
                itemType = itemType,
                projectId = project.id
            )

            val assignableAmount = 6
            testSetupHelper.setupItemsForType(amountToCreate = assignableAmount, itemType = itemType)

            val unusableAmount = 9
            testSetupHelper.setupItemsForType(
                amountToCreate = unusableAmount,
                itemType = itemType,
                condition = ItemCondition.REPAIRABLE
            )

            val wrongStatusAmount = 4
            testSetupHelper.setupItemsForType(
                amountToCreate = wrongStatusAmount,
                itemType = itemType,
                status = ItemStatus.ORDERED
            )

            val alreadyAssignedToDifferentProjectAmount = 3
            testSetupHelper.setupRequiredItemType(
                projectId = otherProject.id,
                itemTypeId = itemType.id,
                alreadyAssignedToDifferentProjectAmount.toLong()
            )
            testSetupHelper.setupItemsForType(
                amountToCreate = alreadyAssignedToDifferentProjectAmount,
                itemType = itemType,
                otherProject.id
            )

            // when
            val assignableItems = cut.readAllAssignableForItemTypeAndProject(
                projectId = project.id,
                itemTypeId = itemType.id,
                pageable = Pageable.unpaged()
            )

            // then
            assignableItems shouldHaveSize assignableAmount
            assignableItems.forEach {
                it.assignableToProjectId shouldBe project.id
                it.condition shouldBeIn arrayOf(
                    InventoryItemCondition.NEW,
                    InventoryItemCondition.WRAPPED,
                    InventoryItemCondition.USED
                )
            }
        }
    }

    context("readAllAssignedForItemTypeAndProject") {
        should("return only items of the given type that are assigned to the given project") {
            // given
            val project = testSetupHelper.setupProject()
            val otherProject = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()

            val assignedAmount = 15
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id, 15)
            testSetupHelper.setupItemsForType(
                amountToCreate = assignedAmount,
                itemType = itemType,
                projectId = project.id
            )

            val assignableAmount = 6
            testSetupHelper.setupItemsForType(amountToCreate = assignableAmount, itemType = itemType)

            val alreadyAssignedToDifferentProjectAmount = 3
            testSetupHelper.setupRequiredItemType(
                projectId = otherProject.id,
                itemTypeId = itemType.id,
                alreadyAssignedToDifferentProjectAmount.toLong()
            )
            testSetupHelper.setupItemsForType(
                amountToCreate = alreadyAssignedToDifferentProjectAmount,
                itemType = itemType,
                otherProject.id
            )

            // when
            val assignedItems = cut.readAllAssignedForItemTypeAndProject(
                projectId = project.id,
                itemTypeId = itemType.id,
                pageable = Pageable.unpaged()
            )

            // then
            assignedItems shouldHaveSize assignedAmount
            assignedItems.forEach {
                it.projectId shouldBe project.id
                it.condition shouldBeIn arrayOf(
                    InventoryItemCondition.NEW,
                    InventoryItemCondition.WRAPPED,
                    InventoryItemCondition.USED
                )
            }
        }
    }

    context("assignToProject") {
        should("assign the item with the given id to the project with the given id") {
            // given
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)
            val item = testSetupHelper.setupItemForType(itemType)

            // when
            val assignedItem = cut.assignToProject(itemId = item.id, newProjectId = project.id)

            // then
            assignedItem.projectId shouldBe project.id
        }

        should("throw WrongNodeNameRuleException when project status not 'planning'") {
            // given
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)
            workflowInteractionService.forceInstanceNode(project.workflowInstanceId, "implementation")
            val item = testSetupHelper.setupItemForType(itemType)

            // when & then
            shouldThrow<WrongNodeNameRuleException> {
                cut.assignToProject(itemId = item.id, newProjectId = project.id)
            }

            // check that project id was not assigned
            val itemToCheck = itemService.getItemById(item.id)
            itemToCheck.assignedProjectId shouldBe null
        }

        should("throw Exception when required amount exceeded") {
            // given
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)
            testSetupHelper.setupItemsForType(itemType = itemType, projectId = project.id)

            val itemToAssign = testSetupHelper.setupItemForType(itemType)

            // when & then
            shouldThrow<RequiredAmountEqualOrSmallerThanAssignedAmountRuleException> {
                cut.assignToProject(itemId = itemToAssign.id, newProjectId = project.id)
            }

            // check that project id was not assigned
            val itemToCheck = itemService.getItemById(itemToAssign.id)
            itemToCheck.assignedProjectId shouldBe null
        }

        should("throw AssignableItemMustBeInStockRuleException when item without status 'IN_STOCK'") {
            // given
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)

            val itemToAssign = testSetupHelper.setupItemForType(itemType, status = ItemStatus.ORDERED)

            // when & then
            shouldThrow<AssignableItemMustBeInStockRuleException> {
                cut.assignToProject(itemId = itemToAssign.id, newProjectId = project.id)
            }

            // check that project id was not assigned
            val itemToCheck = itemService.getItemById(itemToAssign.id)
            itemToCheck.assignedProjectId shouldBe null
        }

        should("throw ItemAlreadyAssignedRuleException when item is already assigned to a project") {
            // given
            val project = testSetupHelper.setupProject()
            val otherProject = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)

            val assignedItem = testSetupHelper.setupItemForType(itemType, projectId = otherProject.id)

            // when & then
            shouldThrow<ItemAlreadyAssignedRuleException> {
                cut.assignToProject(itemId = assignedItem.id, newProjectId = project.id)
            }

            // check that project id was not reassigned
            val itemToCheck = itemService.getItemById(assignedItem.id)
            itemToCheck.assignedProjectId shouldBe otherProject.id
        }
    }

    context("removeFromProject") {
        should("remove its assigned project from the item with the given id") {
            // given
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)
            val item = testSetupHelper.setupItemForType(itemType, projectId = project.id)

            // when
            val itemWithoutProject = cut.removeFromProject(item.id)

            // then
            itemWithoutProject.assignedProjectId shouldBe null
        }

        should("throw WrongNodeNameRuleException when project status not 'planning'") {
            // given
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)
            val item = testSetupHelper.setupItemForType(itemType, projectId = project.id)
            workflowInteractionService.forceInstanceNode(project.workflowInstanceId, "implementation")

            // when & then
            shouldThrow<WrongNodeNameRuleException> {
                cut.removeFromProject(item.id)
            }

            // check that project id was not removed
            val itemToCheck = itemService.getItemById(item.id)
            itemToCheck.assignedProjectId shouldBe project.id
        }
    }

    context("removeAllWithTypeFromProject") {
        should("remove each item of the given type from the project with the given id") {
            // given
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()

            val itemAmount = 5
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id, 5)
            testSetupHelper.setupItemsForType(amountToCreate = itemAmount, itemType = itemType, projectId = project.id)

            // when
            cut.removeAllWithTypeFromProject(itemTypeId = itemType.id, projectId = project.id)

            // then
            val itemsForProject = cut.readAllAssignedForItemTypeAndProject(
                itemTypeId = itemType.id,
                projectId = project.id,
                Pageable.unpaged()
            )
            itemsForProject.shouldBeEmpty()
        }

        should("throw WrongNodeNameRuleException when project status not 'planning'") {
            // given
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()

            val itemAmount = 5
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id, 5)

            testSetupHelper.setupItemsForType(
                amountToCreate = itemAmount,
                itemType = itemType,
                projectId = project.id
            )
            workflowInteractionService.forceInstanceNode(project.workflowInstanceId, "implementation")

            // when
            shouldThrow<WrongNodeNameRuleException> {
                cut.removeAllWithTypeFromProject(itemTypeId = itemType.id, projectId = project.id)
            }

            // then
            val itemsForProject = cut.readAllAssignedForItemTypeAndProject(
                itemTypeId = itemType.id,
                projectId = project.id,
                Pageable.unpaged()
            )
            itemsForProject shouldHaveSize itemAmount
        }
    }
})
