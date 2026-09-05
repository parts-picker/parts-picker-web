package de.partspicker.web.inventory.business

import de.partspicker.web.inventory.business.objects.enums.CheckRequiredItemsResult
import de.partspicker.web.inventory.persistence.RequiredItemTypeRepository
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.test.generators.RequiredItemTypeEntityGenerators
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
    val requiredItemTypeRepositoryMock = mockk<RequiredItemTypeRepository>()
    val inventoryItemReadServiceMock = mockk<InventoryItemReadService>()
    val workflowInteractionServiceMock = mockk<WorkflowInteractionService>()
    val itemTypeRepositoryMock = mockk<ItemTypeRepository>()
    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val cut = InventoryItemService(
        itemRepository = itemRepositoryMock,
        projectRepository = projectRepositoryMock,
        requiredItemTypeReadService = requiredItemTypeReadServiceMock,
        requiredItemTypeRepository = requiredItemTypeRepositoryMock,
        inventoryItemReadService = inventoryItemReadServiceMock,
        workflowInteractionService = workflowInteractionServiceMock,
        itemTypeRepository = itemTypeRepositoryMock,
        orgUnitAccessService = orgUnitAccessServiceMock
    )

    context("checkRequiredItemsAssignedToProject") {
        val projectId = 1L

        should("return NO_REQUIRED when project with the given id has no required item types") {
            // given
            every {
                requiredItemTypeRepositoryMock.findAllByProjectId(projectId, Pageable.unpaged())
            } returns Page.empty()

            // when
            val result = cut.checkRequiredItemsAssignedToProject(projectId)

            // then
            result shouldBe CheckRequiredItemsResult.NO_REQUIRED
        }

        should("return ALL_ASSIGNED when project with the given id has only required items assigned") {
            // given
            val requiredItemTypeEntity = RequiredItemTypeEntityGenerators.generator.single()
            every {
                requiredItemTypeRepositoryMock.findAllByProjectId(projectId, Pageable.unpaged())
            } returns PageImpl(listOf(requiredItemTypeEntity))
            every {
                inventoryItemReadServiceMock.countAssignedForItemTypeAndProject(
                    projectId = requiredItemTypeEntity.id.projectId,
                    itemTypeId = requiredItemTypeEntity.id.itemTypeId
                )
            } returns requiredItemTypeEntity.requiredAmount

            // when
            val result = cut.checkRequiredItemsAssignedToProject(projectId)

            // then
            result shouldBe CheckRequiredItemsResult.ALL_ASSIGNED
        }

        should("return MISSING when project with the given id has missing required items") {
            // given
            val requiredItemTypeEntity = RequiredItemTypeEntityGenerators.generator.single()
            every {
                requiredItemTypeRepositoryMock.findAllByProjectId(projectId, Pageable.unpaged())
            } returns PageImpl(listOf(requiredItemTypeEntity))
            every {
                inventoryItemReadServiceMock.countAssignedForItemTypeAndProject(
                    projectId = requiredItemTypeEntity.id.projectId,
                    itemTypeId = requiredItemTypeEntity.id.itemTypeId
                )
            } returns requiredItemTypeEntity.requiredAmount - 1

            // when
            val result = cut.checkRequiredItemsAssignedToProject(projectId)

            // then
            result shouldBe CheckRequiredItemsResult.MISSING
        }
    }
})
