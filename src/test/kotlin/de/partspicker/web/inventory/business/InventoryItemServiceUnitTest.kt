package de.partspicker.web.inventory.business

import de.partspicker.web.inventory.business.objects.enums.CheckRequiredItemsResult
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.test.generators.inventory.RequiredItemTypeGenerators
import de.partspicker.web.workflow.business.WorkflowInteractionService
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class InventoryItemServiceUnitTest : ShouldSpec({
    val itemRepositoryMock = mockk<ItemRepository>()
    val projectRepositoryMock = mockk<ProjectRepository>()
    val requiredItemTypeReadServiceMock = mockk<RequiredItemTypeReadService>()
    val workflowInteractionServiceMock = mockk<WorkflowInteractionService>()
    val cut = InventoryItemService(
        itemRepository = itemRepositoryMock,
        projectRepository = projectRepositoryMock,
        requiredItemTypeReadService = requiredItemTypeReadServiceMock,
        workflowInteractionService = workflowInteractionServiceMock
    )

    context("checkRequiredItemsAssignedToProject") {
        should("return NO_REQUIRED when project with the given id has no required item types") {
            // given
            val projectId = 1L
            every {
                requiredItemTypeReadServiceMock.readAllByProjectId(projectId, Pageable.unpaged())
            } returns Page.empty()

            // when
            val result = cut.checkRequiredItemsAssignedToProject(projectId)

            // then
            result shouldBe CheckRequiredItemsResult.NO_REQUIRED
        }

        should("return ALL_ASSIGNED when project with the given id has only required items assigned") {
            // given
            val requiredItemTypes = List(5) {
                RequiredItemTypeGenerators.requiredEqualAssignedGenerator.single()
            }
            val projectId = 1L
            every {
                requiredItemTypeReadServiceMock.readAllByProjectId(projectId, Pageable.unpaged())
            } returns PageImpl(requiredItemTypes)

            // when
            val result = cut.checkRequiredItemsAssignedToProject(projectId)

            // then
            result shouldBe CheckRequiredItemsResult.ALL_ASSIGNED
        }

        should("return MISSING when project with the given id has missing required items") {
            // given
            val requiredItemTypes = MutableList(5) {
                RequiredItemTypeGenerators.requiredEqualAssignedGenerator.single()
            }
            requiredItemTypes.add(
                RequiredItemTypeGenerators.generator.single().copy(
                    assignedAmount = 1L,
                    requiredAmount = 2L
                )
            )

            val projectId = 1L
            every {
                requiredItemTypeReadServiceMock.readAllByProjectId(projectId, Pageable.unpaged())
            } returns PageImpl(requiredItemTypes)

            // when
            val result = cut.checkRequiredItemsAssignedToProject(projectId)

            // then
            result shouldBe CheckRequiredItemsResult.MISSING
        }
    }
})
