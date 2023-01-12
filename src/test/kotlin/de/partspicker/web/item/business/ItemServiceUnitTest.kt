package de.partspicker.web.item.business

import de.partspicker.web.item.business.exceptions.ItemNotFoundException
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.business.objects.enums.ItemStatus
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.item.persistance.entities.ItemEntity
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.test.generators.ItemEntityGenerators
import de.partspicker.web.test.generators.ItemGenerators
import de.partspicker.web.test.generators.ItemTypeEntityGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.Optional

class ItemServiceUnitTest : ShouldSpec({

    val itemRepositoryMock = mockk<ItemRepository>()
    val itemTypeRepositoryMock = mockk<ItemTypeRepository>()
    val projectRepositoryMock = mockk<ProjectRepository>()
    val cut = ItemService(
        itemRepository = itemRepositoryMock,
        itemTypeRepository = itemTypeRepositoryMock,
        projectRepository = projectRepositoryMock
    )

    afterTest {
        clearMocks(itemRepositoryMock, itemTypeRepositoryMock, projectRepositoryMock)
    }

    context("create") {
        should("create new item & return it") {
            // given
            val typeEntity = ItemTypeEntityGenerators.generator.next()
            val entity = ItemEntityGenerators.generator.next().copy(type = typeEntity)
            every { itemRepositoryMock.save(any()) } returns entity
            every { itemTypeRepositoryMock.existsById(typeEntity.id) } returns true

            // when
            val returnedItem = cut.create(Item.from(entity))

            // then
            verify(exactly = 1) {
                itemRepositoryMock.save(any())
                itemTypeRepositoryMock.existsById(typeEntity.id)
            }
            returnedItem shouldBe Item.from(entity)
        }

        should("throw ItemTypeNotFoundException when given non-existent type") {
            // given
            val typeEntity = ItemTypeEntityGenerators.generator.next()
            val entity = ItemEntityGenerators.generator.next().copy(type = typeEntity)
            every { itemTypeRepositoryMock.existsById(typeEntity.id) } returns false

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.create(Item.from(entity))
            }

            // then
            verify(exactly = 0) {
                itemRepositoryMock.save(any())
            }

            exception.message shouldBe "ItemType with id ${typeEntity.id} could not be found"
        }
    }

    context("getItems") {
        should("return all items") {
            // given
            val itemsPage: Page<ItemEntity> = PageImpl(
                listOf(
                    ItemEntityGenerators.generator.next(),
                    ItemEntityGenerators.generator.next()
                )
            )
            every { itemRepositoryMock.findAll(Pageable.unpaged()) } returns itemsPage

            // when
            val returnedItems = cut.getItems(Pageable.unpaged())

            // then
            returnedItems shouldBe Item.AsPage.from(itemsPage)
        }

        should("return empty list when no items available") {
            // given
            every { itemRepositoryMock.findAll(Pageable.unpaged()) } returns Page.empty()

            // when
            val returnedItems = cut.getItems(Pageable.unpaged())

            // then
            returnedItems shouldBe Page.empty()
        }
    }

    context("getItem") {
        should("return correct item when given existent id") {
            // given
            val itemEntity = ItemEntityGenerators.generator.next()
            every { itemRepositoryMock.findById(itemEntity.id) } returns Optional.of(itemEntity)

            // when
            val returnedItem = cut.getItemById(itemEntity.id)

            // then
            returnedItem shouldBe Item.from(itemEntity)
        }

        should("throw ItemNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(min = 1).next()
            every { itemRepositoryMock.findById(randomId) } returns Optional.empty()

            // when
            val exception = shouldThrow<ItemNotFoundException> {
                cut.getItemById(randomId)
            }

            // then
            exception.message shouldBe "Item with id $randomId could not be found"
        }
    }

    context("getItemsForItemType") {

        should("return all items with given itemType") {
            // given
            val itemTypeId = Arb.long(min = 1).next()

            val itemsPage: Page<ItemEntity> = PageImpl(
                listOf(
                    ItemEntityGenerators.generator.next(),
                    ItemEntityGenerators.generator.next()
                )
            )

            every { itemRepositoryMock.findAllByTypeId(itemTypeId, Pageable.unpaged()) } returns itemsPage

            // when
            val returnedItems = cut.getItemsForItemType(itemTypeId)

            // then
            returnedItems shouldBe Item.AsPage.from(itemsPage)
        }
    }

    context("update") {
        should("update the item with the given id & return it") {
            // given
            val id = 42L
            val entity = ItemEntityGenerators.generator.next().copy(id = id)
            every { itemRepositoryMock.findById(id) } returns Optional.of(entity)
            every { itemRepositoryMock.save(entity) } returns entity

            val item = Item.from(entity)

            // when
            val updatedItem = cut.update(id = id, item.condition, item.note)

            // then
            updatedItem shouldBe item

            verify(exactly = 1) {
                itemRepositoryMock.save(entity)
            }
        }

        should("throw ItemNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(min = 1).next()
            every { itemRepositoryMock.findById(randomId) } returns Optional.empty()

            // when
            val exception = shouldThrow<ItemNotFoundException> {
                cut.update(randomId, ItemGenerators.randomConditionGen.next(), null)
            }

            // then
            exception.message shouldBe "Item with id $randomId could not be found"
        }
    }

    context("updateAssignedProject") {
        should("update the assignedProjectId of the item with the given id & return it") {
            // given
            val id = 42L
            val entity = ItemEntityGenerators.generator.next().copy(id = id)
            every { itemRepositoryMock.findById(id) } returns Optional.of(entity)
            every { itemRepositoryMock.save(entity) } returnsArgument 0

            val projectId = 4L
            every { projectRepositoryMock.existsById(projectId) } returns true

            // when
            val updatedItem = cut.updateAssignedProject(id, projectId)

            // then
            updatedItem.assignedProjectId shouldBe projectId
            updatedItem.status shouldBe ItemStatus.from(entity.status)
            updatedItem.condition shouldBe ItemCondition.from(entity.condition)
            updatedItem.note shouldBe entity.note

            verify(exactly = 1) {
                itemRepositoryMock.save(entity)
            }
        }

        should("update the assignedProjectId of the item with the given id to null & return it") {
            // given
            val id = 42L
            val entity = ItemEntityGenerators.generator.next().copy(id = id)
            every { itemRepositoryMock.findById(id) } returns Optional.of(entity)
            every { itemRepositoryMock.save(entity) } returnsArgument 0

            // when
            val updatedItem = cut.updateAssignedProject(id, null)

            // then
            updatedItem.assignedProjectId shouldBe null
            updatedItem.status shouldBe ItemStatus.from(entity.status)
            updatedItem.condition shouldBe ItemCondition.from(entity.condition)
            updatedItem.note shouldBe entity.note

            verify(exactly = 1) {
                itemRepositoryMock.save(entity)
            }
        }

        should("throw ItemNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(min = 1).next()
            every { itemRepositoryMock.findById(randomId) } returns Optional.empty()

            // when
            val exception = shouldThrow<ItemNotFoundException> {
                cut.updateAssignedProject(randomId, null)
            }

            // then
            exception.message shouldBe "Item with id $randomId could not be found"
        }

        should("throw ProjectNotFoundException when given non-existent id") {
            // given
            val id = 42L
            val entity = ItemEntityGenerators.generator.next().copy(id = id)
            every { itemRepositoryMock.findById(id) } returns Optional.of(entity)

            val nonExistentProjectId = 666L
            every { projectRepositoryMock.existsById(nonExistentProjectId) } returns false

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.updateAssignedProject(id, nonExistentProjectId)
            }

            // then
            exception.message shouldBe "Project with id $nonExistentProjectId could not be found"
        }
    }

    context("delete") {

        should("delete the item with the given id") {
            // given
            val itemId = Arb.long(min = 1).next()
            every { itemRepositoryMock.existsById(itemId) } returns true
            every { itemRepositoryMock.deleteById(itemId) } returns Unit

            // when
            cut.delete(itemId)

            // then
            verify(exactly = 1) {
                itemRepositoryMock.deleteById(itemId)
            }
        }

        should("throw ItemNotFoundException when given non-existent id") {
            // given
            val itemId = Arb.long(min = 1).next()
            every { itemRepositoryMock.existsById(itemId) } returns false

            // when
            val exception = shouldThrow<ItemNotFoundException> {
                cut.delete(itemId)
            }

            // then
            exception.message shouldBe "Item with id $itemId could not be found"
        }
    }

    context("deleteItemsForItemType") {

        should("delete all items belonging to the given type & return the number of deleted items") {
            // given
            val itemTypeId = Arb.long(min = 1).next()
            val amountOfItems = Arb.long(min = 1).next()
            every { itemRepositoryMock.deleteAllByTypeId(itemTypeId) } returns amountOfItems

            // when
            val amountDeleted = cut.deleteItemsForItemType(itemTypeId)

            // then
            verify(exactly = 1) {
                itemRepositoryMock.deleteAllByTypeId(itemTypeId)
            }
            amountDeleted shouldBe amountOfItems
        }
    }
})
