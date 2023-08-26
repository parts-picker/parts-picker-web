package de.partspicker.web.workflow.business.automated.actions

import de.partspicker.web.inventory.business.InventoryItemService
import de.partspicker.web.inventory.business.objects.enums.CheckRequiredItemsResult
import de.partspicker.web.project.business.ProjectService
import de.partspicker.web.test.generators.ProjectGenerators
import de.partspicker.web.test.generators.workflow.InstanceGenerators
import de.partspicker.web.workflow.business.automated.exceptions.AutomatedActionException
import de.partspicker.web.workflow.business.objects.enums.DisplayType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.mockk

class RequiredItemsAssignedActionUnitTest : ShouldSpec({
    val inventoryItemServiceMock = mockk<InventoryItemService>()
    val projectServiceMock = mockk<ProjectService>()
    val cut = RequiredItemsAssignedAction(
        inventoryItemService = inventoryItemServiceMock,
        projectService = projectServiceMock
    )

    context("execute") {
        should("return no message, display type default & the correct edge when given check returns FULFILLED") {
            // given
            val instance = InstanceGenerators.generator.single()
            val project = ProjectGenerators.generator.single()
            every { projectServiceMock.readByInstanceId(instance.id) } returns project
            every {
                inventoryItemServiceMock.checkRequiredItemsAssignedToProject(project.id)
            } returns CheckRequiredItemsResult.ALL_ASSIGNED

            // when
            val actionResult = cut.execute(instance, emptyList())

            // then
            actionResult.chosenEdgeName shouldBe RequiredItemsAssignedAction.SUCCESSFUL_EDGE
            actionResult.message shouldBe null
            actionResult.displayType shouldBe DisplayType.DEFAULT
        }

        should("return fail message, display type warn & the correct edge when given check returns MISSING") {
            // given
            val instance = InstanceGenerators.generator.single()
            val project = ProjectGenerators.generator.single()
            every { projectServiceMock.readByInstanceId(instance.id) } returns project
            every {
                inventoryItemServiceMock.checkRequiredItemsAssignedToProject(project.id)
            } returns CheckRequiredItemsResult.MISSING

            // when
            val actionResult = cut.execute(instance, emptyList())

            // then
            actionResult.chosenEdgeName shouldBe RequiredItemsAssignedAction.FAILED_EDGE
            actionResult.message shouldBe RequiredItemsAssignedAction.MISSING_ITEMS_MESSAGE
            actionResult.displayType shouldBe DisplayType.WARN
        }

        should("return fail message, display type warn & the correct edge when given check returns NO_REQUIRED") {
            // given
            val instance = InstanceGenerators.generator.single()
            val project = ProjectGenerators.generator.single()
            every { projectServiceMock.readByInstanceId(instance.id) } returns project
            every {
                inventoryItemServiceMock.checkRequiredItemsAssignedToProject(project.id)
            } returns CheckRequiredItemsResult.NO_REQUIRED

            // when
            val actionResult = cut.execute(instance, emptyList())

            // then
            actionResult.chosenEdgeName shouldBe RequiredItemsAssignedAction.FAILED_EDGE
            actionResult.message shouldBe RequiredItemsAssignedAction.NONE_REQUIRED_MESSAGE
            actionResult.displayType shouldBe DisplayType.WARN
        }

        should("throw AutomatedActionException when given instance id that is not associated with a project") {
            // given
            val instance = InstanceGenerators.generator.single()
            every { projectServiceMock.readByInstanceId(instance.id) } returns null

            // when
            val exception = shouldThrow<AutomatedActionException> {
                cut.execute(instance, emptyList())
            }

            // then
            exception.message shouldBe "No project found for the given instanceId ${instance.id}" +
                " - this suggest a workflow configuration error or a serious bug"
        }
    }
})
