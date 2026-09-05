package de.partspicker.web.inventory.business

import de.partspicker.web.inventory.business.objects.enums.CheckRequiredItemsResult
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.orgunit.business.OrgUnitAccessService
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

@Suppress("LongParameterList")
class InventoryItemServiceUnitTest : ShouldSpec({
    val itemRepositoryMock = mockk<ItemRepository>()
    val projectRepositoryMock = mockk<ProjectRepository>()
    val requiredItemTypeReadServiceMock = mockk<RequiredItemTypeReadService>()
    val workflowInteractionServiceMock = mockk<WorkflowInteractionService>()
    val itemTypeRepositoryMock = mockk<ItemTypeRepository>()
    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val cut = InventoryItemService(
        itemRepository = itemRepositoryMock,
        projectRepository = projectRepositoryMock,
        requiredItemTypeReadService = requiredItemTypeReadServiceMock,
        workflowInteractionService = workflowInteractionServiceMock,
        itemTypeRepository = itemTypeRepositoryMock,
        orgUnitAccessService = orgUnitAccessServiceMock
    )

    context("checkRequiredItemsAssignedToProject") {
        should("return NO_REQUIRED when the given project has no required item types") {
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

        should("return ALL_ASSIGNED when every required item type of the given project is fully assigned") {
            // given
            val projectId = 1L
            val requiredItemTypes = List(5) {
                RequiredItemTypeGenerators.requiredEqualAssignedGenerator.single()
            }
            every {
                requiredItemTypeReadServiceMock.readAllByProjectId(projectId, Pageable.unpaged())
            } returns PageImpl(requiredItemTypes)

            // when
            val result = cut.checkRequiredItemsAssignedToProject(projectId)

            // then
            result shouldBe CheckRequiredItemsResult.ALL_ASSIGNED
        }

        should("return MISSING when one required item type of the given project is not fully assigned") {
            // given
            val projectId = 1L
            val requiredItemTypes = MutableList(5) {
                RequiredItemTypeGenerators.requiredEqualAssignedGenerator.single()
            }
            requiredItemTypes.add(
                RequiredItemTypeGenerators.generator.single().copy(assignedAmount = 1L, requiredAmount = 2L)
            )
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
