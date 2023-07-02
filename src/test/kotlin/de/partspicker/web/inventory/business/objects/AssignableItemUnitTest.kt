package de.partspicker.web.inventory.business.objects

import de.partspicker.web.inventory.business.exceptions.AssignableItemMustBeInStockRuleException
import de.partspicker.web.inventory.business.exceptions.InventoryItemMustBeUsableRuleException
import de.partspicker.web.inventory.business.exceptions.ItemAlreadyAssignedRuleException
import de.partspicker.web.inventory.business.objects.enums.InventoryItemCondition
import de.partspicker.web.item.persistance.entities.ItemEntity
import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import de.partspicker.web.item.persistance.entities.enums.ItemConditionEntity
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity
import de.partspicker.web.project.persistance.entities.ProjectEntity
import de.partspicker.web.test.generators.inventory.AssignableItemGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class AssignableItemUnitTest : ShouldSpec({
    context("validation") {
        should("run when everything valid") {
            AssignableItem(
                itemId = 1L,
                itemTypeId = 1L,
                requiredAmount = 10L,
                assignedAmount = 5L,
                assignableToProjectId = 1L,
                assignableToProjectStatus = "planning",
                condition = InventoryItemCondition.WRAPPED
            )
        }

        should("throw IllegalArgumentException when given assignedAmount smaller than zero") {
            shouldThrow<IllegalArgumentException> {
                AssignableItem(
                    itemId = 1L,
                    itemTypeId = 1L,
                    requiredAmount = 10L,
                    assignedAmount = -1L,
                    assignableToProjectId = 1L,
                    assignableToProjectStatus = "planning",
                    condition = InventoryItemCondition.WRAPPED
                )
            }
        }

        should("throw IllegalArgumentException when requiredAmount smaller than zero") {
            shouldThrow<IllegalArgumentException> {
                AssignableItem(
                    itemId = 1L,
                    itemTypeId = 1L,
                    requiredAmount = -1L,
                    assignedAmount = 0L,
                    assignableToProjectId = 1L,
                    assignableToProjectStatus = "planning",
                    condition = InventoryItemCondition.WRAPPED
                )
            }
        }

        should("throw IllegalArgumentException when given required amount smaller assigned amount") {
            shouldThrow<IllegalArgumentException> {
                AssignableItem(
                    itemId = 1L,
                    itemTypeId = 1L,
                    requiredAmount = 3L,
                    assignedAmount = 4L,
                    assignableToProjectId = 1L,
                    assignableToProjectStatus = "planning",
                    condition = InventoryItemCondition.WRAPPED
                )
            }
        }
    }

    context("from") {
        should("run when everything valid") {
            checkAll(AssignableItemGenerators.generator) {
                // just run
            }
        }

        should("throw ItemAlreadyAssignedRuleException when given item with a project assigned already") {
            val itemId = 1L
            val expectedMessage =
                "Item with id '$itemId' may not be assigned to another project while it is already assigned"

            val exception = shouldThrow<ItemAlreadyAssignedRuleException> {
                AssignableItem.from(
                    ItemEntity(
                        id = itemId,
                        type = ItemTypeEntity(id = 1L),
                        assignedProject = ProjectEntity(id = 1L),
                        status = ItemStatusEntity.IN_STOCK,
                        condition = ItemConditionEntity.WRAPPED,
                        note = null
                    ),
                    1,
                    1,
                    1L,
                    "planning"
                )
            }

            exception.message shouldBe expectedMessage
        }

        should("throw AssignableItemMustBeInStockRuleException when given item without status 'IN_STOCK'") {
            val itemId = 1L
            val itemStatus = ItemStatusEntity.RESERVED
            val expectedMessage =
                "Status of item with id $itemId must be IN_STOCK to be assignable but was $itemStatus"

            val exception = shouldThrow<AssignableItemMustBeInStockRuleException> {
                AssignableItem.from(
                    ItemEntity(
                        id = itemId,
                        type = ItemTypeEntity(id = 1L),
                        assignedProject = null,
                        status = itemStatus,
                        condition = ItemConditionEntity.WRAPPED,
                        note = null
                    ),
                    1,
                    1,
                    1L,
                    "planning"
                )
            }

            exception.message shouldBe expectedMessage
        }

        should("throw InventoryItemMustBeUsableRuleException when given item with unusable condition") {
            val itemId = 1L
            val itemCondition = ItemConditionEntity.BROKEN
            val expectedMessage =
                "Item with id $itemId must be usable to be assignable/assigned, but its condition is $itemCondition"

            val exception = shouldThrow<InventoryItemMustBeUsableRuleException> {
                AssignableItem.from(
                    ItemEntity(
                        id = itemId,
                        type = ItemTypeEntity(id = 1L),
                        assignedProject = null,
                        status = ItemStatusEntity.IN_STOCK,
                        condition = itemCondition,
                        note = null
                    ),
                    1,
                    1,
                    1L,
                    "planning"
                )
            }

            exception.message shouldBe expectedMessage
        }
    }
})
