package de.partspicker.web.item.business

import de.partspicker.web.common.business.exceptions.CrossOrgUnitReferenceException
import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.item.business.exceptions.ItemNotFoundException
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.CreateItem
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.business.objects.enums.ItemStatus
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.item.persistance.entities.ItemEntity
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.orgunit.business.exceptions.CreatorOrOrgUnitAccessDeniedException
import de.partspicker.web.orgunit.business.exceptions.OrgUnitAccessDeniedException
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.test.generators.CreationInfoGenerators
import de.partspicker.web.test.generators.ItemEntityGenerators
import de.partspicker.web.test.generators.ItemGenerators
import de.partspicker.web.test.generators.ItemTypeEntityGenerators
import de.partspicker.web.test.generators.OrgUnitEntityGenerators
import de.partspicker.web.test.generators.ProjectEntityGenerators
import de.partspicker.web.test.util.TestConstants.CRUD_REPOSITORY_EXTENSIONS
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
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull

class ItemServiceUnitTest : ShouldSpec({

    val itemRepositoryMock = mockk<ItemRepository>()
    val itemTypeRepositoryMock = mockk<ItemTypeRepository>()
    val projectRepositoryMock = mockk<ProjectRepository>()
    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val cut = ItemService(
        itemRepository = itemRepositoryMock,
        itemTypeRepository = itemTypeRepositoryMock,
        projectRepository = projectRepositoryMock,
        orgUnitAccessService = orgUnitAccessServiceMock
    )

    val orgUnit = OrgUnitEntityGenerators.generator.single()
    val creation = CreationInfoGenerators.generator.single()

    beforeSpec {
        mockkStatic(CRUD_REPOSITORY_EXTENSIONS)
    }

    afterSpec {
        unmockkStatic(CRUD_REPOSITORY_EXTENSIONS)
    }

    beforeTest {
        every { orgUnitAccessServiceMock.requireAtLeast(any(), any()) } returns Unit
        every { orgUnitAccessServiceMock.requireMemberCreatorOrAtLeast(any(), any(), any()) } returns Unit
        every { orgUnitAccessServiceMock.currentUser() } returns creation.createdBy
    }

    afterTest {
        clearMocks(itemRepositoryMock, itemTypeRepositoryMock, projectRepositoryMock, orgUnitAccessServiceMock)
    }

    context("create") {
        val itemTypeEntity = ItemTypeEntityGenerators.generator.single().copy(orgUnit = orgUnit, creation = creation)

        fun createItem(assignedProjectId: Long? = null) = CreateItem(
            itemTypeId = itemTypeEntity.id,
            assignedProjectId = assignedProjectId,
            status = ItemStatus.IN_STOCK,
            condition = ItemCondition.NEW,
            note = "a note"
        )

        should("create new item with project entity & return it when given valid project id") {
            // given
            val itemToCreate = createItem(assignedProjectId = 1L)
            every { itemRepositoryMock.save(any()) } returnsArgument 0
            every { itemTypeRepositoryMock.findByIdOrNull(itemTypeEntity.id) } returns itemTypeEntity
            every {
                projectRepositoryMock.getNullableReferenceById(1L)
            } returns ProjectEntityGenerators.generator.single().copy(id = 1L, orgUnit = orgUnit)

            // when
            val returnedItem = cut.create(itemToCreate)

            // then
            verify {
                itemRepositoryMock.save(any())
            }

            returnedItem.type.id shouldBe itemTypeEntity.id
            returnedItem.assignedProjectId shouldBe 1L
            returnedItem.orgUnitId shouldBe orgUnit.id
        }

        should("create new item & return it") {
            // given
            val itemToCreate = createItem()
            every { itemRepositoryMock.save(any()) } returnsArgument 0
            every { itemTypeRepositoryMock.findByIdOrNull(itemTypeEntity.id) } returns itemTypeEntity

            // when
            val returnedItem = cut.create(itemToCreate)

            // then
            verify {
                itemRepositoryMock.save(any())
                projectRepositoryMock wasNot called
            }

            returnedItem.assignedProjectId shouldBe null
            returnedItem.orgUnitId shouldBe orgUnit.id
        }

        should("throw ItemTypeNotFoundException when given non-existent item type id") {
            // given
            val itemToCreate = createItem()
            every { itemTypeRepositoryMock.findByIdOrNull(itemTypeEntity.id) } returns null

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.create(itemToCreate)
            }

            // then
            verify(exactly = 0) {
                itemRepositoryMock.save(any())
            }

            exception.message shouldBe "ItemType with id ${itemTypeEntity.id} could not be found"
        }

        should("throw ProjectNotFoundException when given non-existent project id") {
            // given
            val itemToCreate = createItem(assignedProjectId = 666L)
            every { itemTypeRepositoryMock.findByIdOrNull(itemTypeEntity.id) } returns itemTypeEntity
            every { projectRepositoryMock.getNullableReferenceById(666L) } returns null

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.create(itemToCreate)
            }

            // then
            verify(exactly = 0) {
                itemRepositoryMock.save(any())
            }

            exception.message shouldBe "Project with id 666 could not be found"
        }

        should("throw CrossOrgUnitReferenceException when the given project is in another org unit") {
            // given
            val itemToCreate = createItem(assignedProjectId = 1L)
            every { itemTypeRepositoryMock.findByIdOrNull(itemTypeEntity.id) } returns itemTypeEntity
            every {
                projectRepositoryMock.getNullableReferenceById(1L)
            } returns ProjectEntityGenerators.generator.single().copy(id = 1L)

            // when & then
            shouldThrow<CrossOrgUnitReferenceException> {
                cut.create(itemToCreate)
            }

            verify(exactly = 0) {
                itemRepositoryMock.save(any())
            }
        }
    }

    context("findAllForOrgUnit") {
        should("refuse & not read when the caller holds nothing in the given org unit") {
            // given
            every {
                orgUnitAccessServiceMock.requireAtLeast(1L, AccessLevel.READ)
            } throws OrgUnitAccessDeniedException(1L, AccessLevel.READ)

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.findAllForOrgUnit(1L, Pageable.unpaged()) }

            verify(exactly = 0) { itemRepositoryMock.findAllByOrgUnitId(any(), any()) }
        }

        should("return all items") {
            // given
            val itemsPage: Page<ItemEntity> = PageImpl(
                listOf(
                    ItemEntityGenerators.generator.next(),
                    ItemEntityGenerators.generator.next()
                )
            )
            every { itemRepositoryMock.findAllByOrgUnitId(1L, Pageable.unpaged()) } returns itemsPage

            // when
            val returnedItems = cut.findAllForOrgUnit(1L, Pageable.unpaged())

            // then
            returnedItems shouldBe Item.AsPage.from(itemsPage)
        }

        should("return empty list when no items available") {
            // given
            every { itemRepositoryMock.findAllByOrgUnitId(1L, Pageable.unpaged()) } returns Page.empty()

            // when
            val returnedItems = cut.findAllForOrgUnit(1L, Pageable.unpaged())

            // then
            returnedItems shouldBe Page.empty()
        }
    }

    context("getById") {
        should("return correct item when given existent id") {
            // given
            val itemEntity = ItemEntityGenerators.generator.next()
            every { itemRepositoryMock.findByIdOrNull(itemEntity.id) } returns itemEntity

            // when
            val returnedItem = cut.getById(itemEntity.id)

            // then
            returnedItem shouldBe Item.from(itemEntity)
        }

        should("throw ItemNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(min = 1).next()
            every { itemRepositoryMock.findByIdOrNull(randomId) } returns null

            // when
            val exception = shouldThrow<ItemNotFoundException> {
                cut.getById(randomId)
            }

            // then
            exception.message shouldBe "Item with id $randomId could not be found"
        }
    }

    context("findAllForItemType") {

        should("return all items with given itemType") {
            // given
            val itemTypeId = Arb.long(min = 1).next()

            val itemsPage: Page<ItemEntity> = PageImpl(
                listOf(
                    ItemEntityGenerators.generator.next(),
                    ItemEntityGenerators.generator.next()
                )
            )

            every {
                itemTypeRepositoryMock.findByIdOrNull(itemTypeId)
            } returns ItemTypeEntityGenerators.generator.next().copy(id = itemTypeId)
            every { itemRepositoryMock.findAllByTypeId(itemTypeId, Pageable.unpaged()) } returns itemsPage

            // when
            val returnedItems = cut.findAllForItemType(itemTypeId)

            // then
            returnedItems shouldBe Item.AsPage.from(itemsPage)
        }
    }

    context("update") {
        should("update the item with the given id & return it") {
            // given
            val id = 42L
            val entity = ItemEntityGenerators.generator.next().copy(id = id)
            every { itemRepositoryMock.findByIdOrNull(id) } returns entity
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
            every { itemRepositoryMock.findByIdOrNull(randomId) } returns null

            // when
            val exception = shouldThrow<ItemNotFoundException> {
                cut.update(randomId, ItemGenerators.randomConditionGen.next(), null)
            }

            // then
            exception.message shouldBe "Item with id $randomId could not be found"
        }
    }

    context("delete") {

        should("refuse & not delete when the caller neither created the item nor maintains its org unit") {
            // given
            val itemEntity = ItemEntityGenerators.generator.next()
            every { itemRepositoryMock.findByIdOrNull(itemEntity.id) } returns itemEntity
            every {
                orgUnitAccessServiceMock.requireMemberCreatorOrAtLeast(any(), any(), AccessLevel.MAINTAIN)
            } throws CreatorOrOrgUnitAccessDeniedException(itemEntity.orgUnit.id, AccessLevel.MAINTAIN)

            // when & then
            shouldThrow<CreatorOrOrgUnitAccessDeniedException> { cut.delete(itemEntity.id) }

            verify(exactly = 0) { itemRepositoryMock.delete(any()) }
        }

        should("delete the item with the given id") {
            // given
            val itemId = Arb.long(min = 1).next()
            val itemEntity = ItemEntityGenerators.generator.next().copy(id = itemId)
            every { itemRepositoryMock.findByIdOrNull(itemId) } returns itemEntity
            every { itemRepositoryMock.delete(itemEntity) } returns Unit

            // when
            cut.delete(itemId)

            // then
            verify(exactly = 1) {
                itemRepositoryMock.delete(itemEntity)
            }
        }

        should("throw ItemNotFoundException when given non-existent id") {
            // given
            val itemId = Arb.long(min = 1).next()
            every { itemRepositoryMock.findByIdOrNull(itemId) } returns null

            // when
            val exception = shouldThrow<ItemNotFoundException> {
                cut.delete(itemId)
            }

            // then
            exception.message shouldBe "Item with id $itemId could not be found"
        }
    }
})
