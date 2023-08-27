package de.partspicker.web.item.business

import de.partspicker.web.item.business.exceptions.ItemNotFoundException
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.item.persistance.entities.ItemEntity
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.test.generators.ItemEntityGenerators
import de.partspicker.web.test.generators.ItemGenerators
import de.partspicker.web.test.generators.ProjectEntityGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.single
import io.mockk.called
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
        should("create new item with project entity & return it when given valid project id") {
            // given
            val item = ItemGenerators.generator.single().copy(assignedProjectId = 1L)
            every { itemRepositoryMock.save(any()) } returnsArgument 0
            every { itemTypeRepositoryMock.existsById(item.type.id) } returns true
            every {
                projectRepositoryMock.getNullableReferenceById(item.assignedProjectId!!)
            } returns ProjectEntityGenerators.generator.single().copy(id = item.assignedProjectId!!)

            // when
            val returnedItem = cut.create(item)

            // then
            verify {
                itemRepositoryMock.save(any())
            }

            returnedItem shouldBe item
        }

        should("create new item & return it") {
            // given
            val item = ItemGenerators.generator.single().copy(assignedProjectId = null)
            every { itemRepositoryMock.save(any()) } returnsArgument 0
            every { itemTypeRepositoryMock.existsById(item.type.id) } returns true

            // when
            val returnedItem = cut.create(item)

            // then
            verify {
                itemRepositoryMock.save(any())
                projectRepositoryMock wasNot called
            }

            returnedItem shouldBe item
        }

        should("throw ItemTypeNotFoundException when given non-existent item type id") {
            // given
            val item = ItemGenerators.generator.single()
            every { itemTypeRepositoryMock.existsById(item.type.id) } returns false

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.create(item)
            }

            // then
            verify(exactly = 0) {
                itemRepositoryMock.save(any())
            }

            exception.message shouldBe "ItemType with id ${item.type.id} could not be found"
        }

        should("throw ProjectNotFoundException when given non-existent project id") {
            // given
            val item = ItemGenerators.generator.single().copy(assignedProjectId = 666L)
            every { itemTypeRepositoryMock.existsById(item.type.id) } returns true

            every { projectRepositoryMock.getNullableReferenceById(item.assignedProjectId!!) } returns null

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.create(item)
            }

            // then
            verify(exactly = 0) {
                itemRepositoryMock.save(any())
            }

            exception.message shouldBe "Project with id ${item.assignedProjectId} could not be found"
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
