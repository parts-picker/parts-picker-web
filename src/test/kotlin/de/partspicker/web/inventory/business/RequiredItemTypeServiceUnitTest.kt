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
import de.partspicker.web.test.generators.id
import de.partspicker.web.test.generators.inventory.CreateOrUpdateRequiredItemTypeGenerators
import de.partspicker.web.test.generators.inventory.RequiredItemTypeGenerators
import de.partspicker.web.test.generators.workflow.InstanceEntityGenerators
import de.partspicker.web.workflow.business.WorkflowInteractionService
import de.partspicker.web.workflow.business.exceptions.InstanceInactiveException
import de.partspicker.web.workflow.persistence.entities.nodes.UserActionNodeEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.single
import io.mockk.called
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import io.mockk.verify
import java.util.stream.Stream

class RequiredItemTypeServiceUnitTest : ShouldSpec({

    val requiredItemTypeRepositoryMock = mockk<RequiredItemTypeRepository>()
    val requiredItemTypeReadServiceMock = mockk<RequiredItemTypeReadService>()
    val projectRepositoryMock = mockk<ProjectRepository>()
    val itemTypeRepositoryMock = mockk<ItemTypeRepository>()
    val workflowInteractionsServiceMock = mockk<WorkflowInteractionService>()
    val inventoryItemReadServiceMock = mockk<InventoryItemReadService>()
    val inventoryItemServiceMock = mockk<InventoryItemService>()
    val cut = RequiredItemTypeService(
        requiredItemTypeRepository = requiredItemTypeRepositoryMock,
        requiredItemTypeReadService = requiredItemTypeReadServiceMock,
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
            val instanceEntity = InstanceEntityGenerators.generator.single().copy(currentNode = nodeEntity).copy(
                active = true
            )
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
            val instanceEntity = InstanceEntityGenerators.generator.single().copy(
                currentNode = nodeEntity,
                active = true
            )
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

        should("throw WrongNodeNameRuleException when given project without status 'planning'") {
            // given
            val createOrUpdateRequiredItemType = CreateOrUpdateRequiredItemTypeGenerators.generator.single()

            val nodeEntity = UserActionNodeEntity(
                id = 1L,
                workflow = mockk(),
                name = "not-planning",
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
            val exception = shouldThrow<WrongNodeNameRuleException> {
                cut.createOrUpdate(createOrUpdateRequiredItemType)
            }

            // then
            exception.message shouldBe
                "Expected project status is planning, but actual status is ${nodeEntity.name}"
        }

        should("throw InstanceInactiveException when given inactive project") {
            // given
            val createOrUpdateRequiredItemType = CreateOrUpdateRequiredItemTypeGenerators.generator.single()

            val nodeEntity = UserActionNodeEntity(
                id = 1L,
                workflow = mockk(),
                name = "planning",
                displayName = "displayName"
            )
            val instanceEntity = InstanceEntityGenerators.generator.single().copy(
                currentNode = nodeEntity,
                active = false
            )
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
            val exception = shouldThrow<InstanceInactiveException> {
                cut.createOrUpdate(createOrUpdateRequiredItemType)
            }

            // then
            exception.message shouldBe
                "The instance with the given id ${instanceEntity.id} is inactive & cannot be modified"
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

    context("deleteAllByProjectId") {
        should("delete all required item types with the given project id") {
            // given
            val projectId = Arb.id().single()

            every { projectRepositoryMock.existsById(projectId) } returns true

            every { requiredItemTypeRepositoryMock.deleteAllByProjectId(projectId) } just runs

            // when
            cut.deleteAllByProjectId(projectId)

            // then
            verify { requiredItemTypeRepositoryMock.deleteAllByProjectId(projectId) }
        }

        should("throw ProjectNotFoundException when given non-existent id") {
            // given
            val projectId = Arb.id().single()

            every { projectRepositoryMock.existsById(projectId) } returns false

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.deleteAllByProjectId(projectId)
            }

            // then
            exception.message shouldBe "Project with id $projectId could not be found"

            verify {
                requiredItemTypeRepositoryMock wasNot called
            }
        }
    }

    context("copyAllToTargetProjectByProjectId") {
        should("create copies of all required item types of the source project for target project") {
            // given
            val sourceProjectId = Arb.id().single()
            every { projectRepositoryMock.existsById(sourceProjectId) } returns true

            val targetProjectId = Arb.id().single()
            every { projectRepositoryMock.existsById(targetProjectId) } returns true

            val requiredItemTypes = arrayOf(
                RequiredItemTypeGenerators.generator.single(),
                RequiredItemTypeGenerators.generator.single()
            )
            every {
                requiredItemTypeReadServiceMock.streamAllByProjectId(sourceProjectId)
            } returns Stream.of(*requiredItemTypes)

            val cutSpy = spyk(cut)
            every { cutSpy.createOrUpdate(any()) } returns RequiredItemTypeGenerators.generator.single()

            // when
            cutSpy.copyAllToTargetProjectByProjectId(sourceProjectId, targetProjectId)

            // then
            verify(exactly = requiredItemTypes.size) { cutSpy.createOrUpdate(any()) }
        }

        should("throw ProjectNotFoundException when given non-existent source project id") {
            // given
            val sourceProjectId = Arb.id().single()

            every { projectRepositoryMock.existsById(sourceProjectId) } returns false

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.copyAllToTargetProjectByProjectId(sourceProjectId, 1L)
            }

            // then
            exception.message shouldBe "Project with id $sourceProjectId could not be found"

            verify {
                requiredItemTypeRepositoryMock wasNot called
            }
        }

        should("throw ProjectNotFoundException when given non-existent target project id") {
            // given
            val sourceProjectId = Arb.id().single()
            every { projectRepositoryMock.existsById(sourceProjectId) } returns true

            val targetProjectId = Arb.id().single()
            every { projectRepositoryMock.existsById(targetProjectId) } returns false

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.copyAllToTargetProjectByProjectId(sourceProjectId, targetProjectId)
            }

            // then
            exception.message shouldBe "Project with id $targetProjectId could not be found"

            verify {
                requiredItemTypeRepositoryMock wasNot called
            }
        }
    }
})
