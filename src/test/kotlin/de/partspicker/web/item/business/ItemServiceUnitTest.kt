package de.partspicker.web.item.business

import de.partspicker.web.item.business.exceptions.ItemNotFoundException
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.test.generators.ItemEntityGenerators
import de.partspicker.web.test.generators.ItemTypeEntityGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class ItemServiceUnitTest : ShouldSpec({

    val itemRepositoryMock = mockk<ItemRepository>()
    val itemTypeRepositoryMock = mockk<ItemTypeRepository>()
    val cut = ItemService(
        itemRepository = itemRepositoryMock,
        itemTypeRepository = itemTypeRepositoryMock
    )

    context("create") {
        should("create new item & return it") {
            // given
            val typeEntity = ItemTypeEntityGenerators.generator.next()
            val entity = ItemEntityGenerators.generator.next().copy(type = typeEntity)
            every { itemRepositoryMock.save(entity) } returns entity
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
            exception.message shouldBe "ItemType with id ${typeEntity.id} could not be found"
        }
    }

    context("getItems") {
        should("return all items") {
            // given
            val itemEntities = listOf(
                ItemEntityGenerators.generator.next(),
                ItemEntityGenerators.generator.next()
            )
            every { itemRepositoryMock.findAll() } returns itemEntities

            // when
            val returnedItems = cut.getItems()

            // then
            returnedItems shouldBe Item.AsList.from(itemEntities)
        }

        should("return empty list when no items available") {
            // given
            every { itemRepositoryMock.findAll() } returns emptyList()

            // when
            val returnedItems = cut.getItems()

            // then
            returnedItems shouldBe emptyList()
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

            val itemEntities = listOf(
                ItemEntityGenerators.generator.next(),
                ItemEntityGenerators.generator.next()
            )

            every { itemRepositoryMock.findAllByTypeId(itemTypeId) } returns itemEntities

            // when
            val returnedItems = cut.getItemsForItemType(itemTypeId)

            // then
            returnedItems shouldBe Item.AsList.from(itemEntities)
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
