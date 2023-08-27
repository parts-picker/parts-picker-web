package de.partspicker.web.inventory.business

import de.partspicker.web.common.business.exceptions.WrongNodeNameRuleException
import de.partspicker.web.inventory.business.exceptions.RequiredItemTypeAmountSmallerThanAssignedException
import de.partspicker.web.inventory.persistence.RequiredItemTypeRepository
import de.partspicker.web.inventory.persistence.embeddableids.RequiredItemTypeId
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.test.generators.ProjectEntityGenerators
import de.partspicker.web.test.generators.inventory.CreateOrUpdateRequiredItemTypeGenerators
import de.partspicker.web.test.generators.workflow.InstanceEntityGenerators
import de.partspicker.web.workflow.business.WorkflowInteractionService
import de.partspicker.web.workflow.persistence.entities.nodes.UserActionNodeEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.single
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class RequiredItemTypeServiceUnitTest : ShouldSpec({

    val requiredItemTypeRepositoryMock = mockk<RequiredItemTypeRepository>()
    val projectRepositoryMock = mockk<ProjectRepository>()
    val itemTypeRepositoryMock = mockk<ItemTypeRepository>()
    val workflowInteractionsServiceMock = mockk<WorkflowInteractionService>()
    val inventoryItemReadServiceMock = mockk<InventoryItemReadService>()
    val inventoryItemServiceMock = mockk<InventoryItemService>()
    val cut = RequiredItemTypeService(
        requiredItemTypeRepository = requiredItemTypeRepositoryMock,
        projectRepository = projectRepositoryMock,
        itemTypeRepository = itemTypeRepositoryMock,
        workflowInteractionService = workflowInteractionsServiceMock,
        inventoryItemReadService = inventoryItemReadServiceMock,
        inventoryItemService = inventoryItemServiceMock
    )

    afterTest {
        clearMocks(requiredItemTypeRepositoryMock)
    }

    context("createOrUpdate") {
        should("create or update a required item type & return it") {
            // given
            val createOrUpdateRequiredItemType = CreateOrUpdateRequiredItemTypeGenerators.generator.single()

            val nodeEntity = UserActionNodeEntity(
                id = 1L,
                workflow = mockk(),
                name = "planning",
                displayName = "displayName"
            )
            val instanceEntity = InstanceEntityGenerators.generator.single().copy(currentNode = nodeEntity)
            every { projectRepositoryMock.getNullableReferenceById(createOrUpdateRequiredItemType.projectId) } returns
                ProjectEntityGenerators.generator.single().copy(
                    workflowInstance = instanceEntity,
                    id = createOrUpdateRequiredItemType.projectId
                )

            every { itemTypeRepositoryMock.existsById(createOrUpdateRequiredItemType.itemTypeId) } returns true
            every { requiredItemTypeRepositoryMock.save(any()) } returnsArgument 0
            every {
                inventoryItemReadServiceMock.countAssignedForItemTypeAndProject(
                    createOrUpdateRequiredItemType.itemTypeId,
                    createOrUpdateRequiredItemType.projectId
                )
            } returns 0L

            // when
            val returnedRequiredItemType = cut.createOrUpdate(createOrUpdateRequiredItemType)

            // then
            verify(exactly = 1) {
                requiredItemTypeRepositoryMock.save(any())
            }

            returnedRequiredItemType.projectId shouldBe createOrUpdateRequiredItemType.projectId
            returnedRequiredItemType.itemType.id shouldBe createOrUpdateRequiredItemType.itemTypeId
            returnedRequiredItemType.requiredAmount shouldBe createOrUpdateRequiredItemType.requiredAmount
        }

        should("throw ProjectNotFoundException when given non-existent project") {
            // given
            val createOrUpdateRequiredItemType = CreateOrUpdateRequiredItemTypeGenerators.generator.single()

            every {
                projectRepositoryMock.getNullableReferenceById(createOrUpdateRequiredItemType.projectId)
            } returns null

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.createOrUpdate(createOrUpdateRequiredItemType)
            }

            // then
            exception.message shouldBe "Project with id ${createOrUpdateRequiredItemType.projectId} could not be found"
        }

        should("throw ItemTypeNotFoundException when given non-existent itemType") {
            // given
            val createOrUpdateRequiredItemType = CreateOrUpdateRequiredItemTypeGenerators.generator.single()

            val nodeEntity = UserActionNodeEntity(
                id = 1L,
                workflow = mockk(),
                name = "planning",
                displayName = "displayName"
            )
            val instanceEntity = InstanceEntityGenerators.generator.single().copy(currentNode = nodeEntity)
            every { projectRepositoryMock.getNullableReferenceById(createOrUpdateRequiredItemType.projectId) } returns
                ProjectEntityGenerators.generator.single().copy(
                    workflowInstance = instanceEntity,
                    id = createOrUpdateRequiredItemType.projectId
                )

            every { itemTypeRepositoryMock.existsById(any()) } returns false
            every {
                inventoryItemReadServiceMock.countAssignedForItemTypeAndProject(
                    createOrUpdateRequiredItemType.itemTypeId,
                    createOrUpdateRequiredItemType.projectId
                )
            } returns 0L

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.createOrUpdate(createOrUpdateRequiredItemType)
            }

            // then
            exception.message shouldBe
                "ItemType with id ${createOrUpdateRequiredItemType.itemTypeId} could not be found"
        }

        should("throw RequiredItemTypeAmountSmallerThanAssignedException when given required smaller assigned") {
            // given
            val assignedAmount = 5L
            val requiredAmount = 4L
            val createOrUpdateRequiredItemType = CreateOrUpdateRequiredItemTypeGenerators.generator.single().copy(
                requiredAmount = requiredAmount
            )

            every { projectRepositoryMock.getNullableReferenceById(createOrUpdateRequiredItemType.projectId) } returns
                mockk()

            every {
                inventoryItemReadServiceMock.countAssignedForItemTypeAndProject(
                    createOrUpdateRequiredItemType.itemTypeId,
                    createOrUpdateRequiredItemType.projectId
                )
            } returns assignedAmount

            // when & then
            assignedAmount shouldBeGreaterThan requiredAmount
            shouldThrow<RequiredItemTypeAmountSmallerThanAssignedException> {
                cut.createOrUpdate(createOrUpdateRequiredItemType)
            }
        }

        should("throw WrongNodeNameRuleException when project status is not 'planning'") {
            // given
            val createOrUpdateRequiredItemType = CreateOrUpdateRequiredItemTypeGenerators.generator.single()

            val nodeEntity = UserActionNodeEntity(
                id = 1L,
                workflow = mockk(),
                name = "something else",
                displayName = "displayName"
            )
            val instanceEntity = InstanceEntityGenerators.generator.single().copy(currentNode = nodeEntity)
            every { projectRepositoryMock.getNullableReferenceById(createOrUpdateRequiredItemType.projectId) } returns
                ProjectEntityGenerators.generator.single().copy(
                    workflowInstance = instanceEntity,
                    id = createOrUpdateRequiredItemType.projectId
                )

            every { itemTypeRepositoryMock.existsById(any()) } returns false
            every {
                inventoryItemReadServiceMock.countAssignedForItemTypeAndProject(
                    createOrUpdateRequiredItemType.itemTypeId,
                    createOrUpdateRequiredItemType.projectId
                )
            } returns 0L

            // when & then
            shouldThrow<WrongNodeNameRuleException> {
                cut.createOrUpdate(createOrUpdateRequiredItemType)
            }
        }
    }

    context("delete") {
        should("delete the requiredItemType with the given projectId & itemTypeId") {
            // given
            val projectId = Arb.long(min = 1).next()
            val itemTypeId = Arb.long(min = 1).next()

            every { projectRepositoryMock.existsById(projectId) } returns true
            every { itemTypeRepositoryMock.existsById(itemTypeId) } returns true
            every { requiredItemTypeRepositoryMock.deleteById(RequiredItemTypeId(projectId, itemTypeId)) } returns Unit
            every {
                workflowInteractionsServiceMock.readProjectStatus(projectId)
            } returns "planning"
            every {
                inventoryItemServiceMock.removeAllWithTypeFromProject(
                    itemTypeId = itemTypeId,
                    projectId = projectId
                )
            } returns Unit

            // when
            cut.delete(projectId, itemTypeId)

            // then
            verify(exactly = 1) {
                requiredItemTypeRepositoryMock.deleteById(RequiredItemTypeId(projectId, itemTypeId))
                inventoryItemServiceMock.removeAllWithTypeFromProject(
                    itemTypeId = itemTypeId,
                    projectId = projectId
                )
            }
        }

        should("throw ProjectNotFoundException when given non-existent id") {
            // given
            val projectId = Arb.long(min = 1).next()
            val itemTypeId = Arb.long(min = 1).next()

            every { projectRepositoryMock.existsById(projectId) } returns false

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.delete(projectId, itemTypeId)
            }

            // then
            exception.message shouldBe "Project with id $projectId could not be found"

            verify(exactly = 0) {
                requiredItemTypeRepositoryMock.deleteById(any())
            }
        }

        should("throw ItemTypeNotFoundException when given non-existent id") {
            // given
            val projectId = Arb.long(min = 1).next()
            val itemTypeId = Arb.long(min = 1).next()

            every { projectRepositoryMock.existsById(projectId) } returns true
            every {
                workflowInteractionsServiceMock.readProjectStatus(projectId)
            } returns "planning"
            every { itemTypeRepositoryMock.existsById(itemTypeId) } returns false

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.delete(projectId, itemTypeId)
            }

            // then
            exception.message shouldBe "ItemType with id $itemTypeId could not be found"

            verify(exactly = 0) {
                requiredItemTypeRepositoryMock.deleteById(any())
            }
        }
    }
})
