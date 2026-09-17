package de.partspicker.web.inventory.business

import de.partspicker.web.common.business.exceptions.WrongNodeNameRuleException
import de.partspicker.web.test.util.TestSetupHelper
import de.partspicker.web.workflow.business.WorkflowMigrationService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("integration")
@Transactional
class RequiredItemTypeServiceIntTest(
    private val cut: RequiredItemTypeService,
    // helpers
    private val inventoryItemService: InventoryItemService,
    private val requiredItemTypeReadService: RequiredItemTypeReadService,
    private val workflowMigrationService: WorkflowMigrationService,
    private val testSetupHelper: TestSetupHelper
) : ShouldSpec({
    context("delete") {
        should("remove the requirement & unassign each item of the given type from the given project") {
            // given
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()

            val itemAmount = 5
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id, 5)
            testSetupHelper.setupItemsForType(amountToCreate = itemAmount, itemType = itemType, projectId = project.id)

            // when
            cut.delete(projectId = project.id, itemTypeId = itemType.id)

            // then
            requiredItemTypeReadService.readAllByProjectId(project.id, Pageable.unpaged()).shouldBeEmpty()

            val itemsForProject = inventoryItemService.readAllAssignedForItemTypeAndProject(
                itemTypeId = itemType.id,
                projectId = project.id,
                Pageable.unpaged()
            )
            itemsForProject.shouldBeEmpty()
        }

        should("throw WrongNodeNameRuleException when project status not 'planning'") {
            // given
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()

            val itemAmount = 5
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id, 5)

            testSetupHelper.setupItemsForType(
                amountToCreate = itemAmount,
                itemType = itemType,
                projectId = project.id
            )
            workflowMigrationService.forceSetInstanceNodeWithinWorkflow(
                project.workflowInstanceId,
                "implementation"
            )

            // when
            shouldThrow<WrongNodeNameRuleException> {
                cut.delete(projectId = project.id, itemTypeId = itemType.id)
            }

            // then
            val itemsForProject = inventoryItemService.readAllAssignedForItemTypeAndProject(
                itemTypeId = itemType.id,
                projectId = project.id,
                Pageable.unpaged()
            )
            itemsForProject shouldHaveSize itemAmount
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
