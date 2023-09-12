package de.partspicker.web.workflow.business.automated.actions

import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.project.business.ProjectService
import de.partspicker.web.test.generators.ProjectGenerators
import de.partspicker.web.test.generators.workflow.InstanceGenerators
import de.partspicker.web.workflow.business.automated.exceptions.InstanceNotRelatedToProjectException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify

class ProjectCompletionInventoryUpdateActionUnitTest : ShouldSpec({
    val projectServiceMock = mockk<ProjectService>()
    val itemRepositoryMock = mockk<ItemRepository>()
    val cut = ProjectCompletionInventoryUpdateAction(
        projectService = projectServiceMock,
        itemRepository = itemRepositoryMock
    )

    context("execute") {
        should("return the correct edge & set status to used for all assigned items when called") {
            // given
            val instance = InstanceGenerators.generator.single()
            val project = ProjectGenerators.generator.single()

            every { projectServiceMock.readByInstanceId(instance.id) } returns project
            every { itemRepositoryMock.updateSetStatusUsedByAssignedProjectId(project.id) } just runs

            // when
            val result = cut.execute(instance, emptyList())

            // then
            result.chosenEdgeName shouldBe ProjectCompletionInventoryUpdateAction.SUCCESSFUL_EDGE

            verify { itemRepositoryMock.updateSetStatusUsedByAssignedProjectId(project.id) }
        }

        should("throw InstanceNotRelatedToProjectException when given non-existent project id") {
            // given
            val instance = InstanceGenerators.generator.single()

            every { projectServiceMock.readByInstanceId(instance.id) } returns null

            // when
            val exception = shouldThrow<InstanceNotRelatedToProjectException> { cut.execute(instance, emptyList()) }

            // then
            exception.message shouldBe "No project found for the given instanceId ${instance.id}" +
                " - this suggest a workflow configuration error or a serious bug"
        }
    }
})
