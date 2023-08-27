package de.partspicker.web.workflow.business.automated

import de.partspicker.web.test.generators.workflow.NodeGenerators
import de.partspicker.web.workflow.business.InstanceService
import de.partspicker.web.workflow.business.InstanceValueReadService
import de.partspicker.web.workflow.business.WorkflowInteractionService
import de.partspicker.web.workflow.business.automated.AutomatedActionService.Companion.MAX_AUTOMATED_INSTANCE_HITS
import de.partspicker.web.workflow.business.automated.actions.AutomatedAction
import de.partspicker.web.workflow.business.automated.actions.AutomatedActionResult
import de.partspicker.web.workflow.business.automated.exceptions.AutomatedActionException
import de.partspicker.web.workflow.business.objects.Edge
import de.partspicker.web.workflow.business.objects.Instance
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single
import io.mockk.called
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.context.ApplicationContext

class AutomatedActionServiceUnitTest : ShouldSpec({
    val instanceServiceMock = mockk<InstanceService>()
    val applicationContextMock = mockk<ApplicationContext>()
    val workflowInteractionServiceMock = mockk<WorkflowInteractionService>()
    val instanceValueReadServiceMock = mockk<InstanceValueReadService>()
    val cut = AutomatedActionService(
        instanceService = instanceServiceMock,
        applicationContext = applicationContextMock,
        workflowInteractionService = workflowInteractionServiceMock,
        instanceValueReadService = instanceValueReadServiceMock
    )

    afterEach { clearMocks(workflowInteractionServiceMock) }

    context("executeBatch") {
        should("return the amount of executed instances when given a list of valid instances to execute") {
            // given
            val instances = listOf(
                Instance(
                    id = 1L,
                    currentNode = NodeGenerators.automatedActionNodeGenerator.single(),
                    active = true,
                    workflowId = 1L
                )
            )
            every { instanceServiceMock.readInstancesWaitingForAutomatedRunner(MAX_AUTOMATED_INSTANCE_HITS) } returns
                instances

            val edgeName = "edge"
            val automatedActionMock = mockk<AutomatedAction>()
            every { automatedActionMock.execute(instances[0], any()) } returns AutomatedActionResult(edgeName)
            every {
                applicationContextMock.getBean(AutomatedAction::class.java, any())
            } returns automatedActionMock

            every { instanceValueReadServiceMock.readAllForInstance(instances[0].id) } returns emptyList()

            val edgeId = 1L
            every { workflowInteractionServiceMock.readEdgesBySourceNodeId(instances[0].currentNode.id) } returns
                setOf(
                    Edge(
                        id = edgeId,
                        workflowId = 1L,
                        sourceNodeId = 1L,
                        targetNodeId = 2L,
                        name = edgeName,
                        displayName = "display name"
                    )
                )

            every {
                workflowInteractionServiceMock.advanceInstanceNodeBySystem(
                    instances[0].id,
                    edgeId,
                    any(),
                    any(),
                    any()
                )
            } returns mockk()

            // when
            val result = cut.executeBatch()

            // then
            result shouldBe 1

            verify {
                workflowInteractionServiceMock.advanceInstanceNodeBySystem(
                    instances[0].id,
                    edgeId,
                    any(),
                    any(),
                    any()
                )
            }
        }

        should("return 0 when no instances have an automated node as current node") {
            // given
            every { instanceServiceMock.readInstancesWaitingForAutomatedRunner(MAX_AUTOMATED_INSTANCE_HITS) } returns
                emptyList()

            // when
            val result = cut.executeBatch()

            // then
            result shouldBe 0

            verify { workflowInteractionServiceMock wasNot called }
        }

        should("skip an inactive instance") {
            // given
            val instances = listOf(
                Instance(
                    id = 1L,
                    currentNode = NodeGenerators.automatedActionNodeGenerator.single(),
                    active = false,
                    workflowId = 1L
                )
            )
            every { instanceServiceMock.readInstancesWaitingForAutomatedRunner(MAX_AUTOMATED_INSTANCE_HITS) } returns
                instances

            // when
            val result = cut.executeBatch()

            // then
            result shouldBe 0

            verify { workflowInteractionServiceMock wasNot called }
        }

        should("throw AutomatedActionException when given instance without current node being automated") {
            // given
            val instances = listOf(
                Instance(
                    id = 1L,
                    currentNode = NodeGenerators.userActionNodeGenerator.single(),
                    active = true,
                    workflowId = 1L
                )
            )
            every { instanceServiceMock.readInstancesWaitingForAutomatedRunner(MAX_AUTOMATED_INSTANCE_HITS) } returns
                instances

            // when
            val exception = shouldThrow<AutomatedActionException> { cut.executeBatch() }

            // then
            exception.message shouldBe
                "The instance with id ${instances[0].id} a non-automated node cannot be executed automatically"

            verify { workflowInteractionServiceMock wasNot called }
        }

        should("throw AutomatedActionException when automated action returns non-existent edge name") {
            // given
            val instances = listOf(
                Instance(
                    id = 1L,
                    currentNode = NodeGenerators.automatedActionNodeGenerator.single(),
                    active = true,
                    workflowId = 1L
                )
            )
            every { instanceServiceMock.readInstancesWaitingForAutomatedRunner(MAX_AUTOMATED_INSTANCE_HITS) } returns
                instances

            val edgeName = "edge"
            val automatedActionMock = mockk<AutomatedAction>()
            every { automatedActionMock.execute(instances[0], any()) } returns AutomatedActionResult(edgeName)
            every {
                applicationContextMock.getBean(AutomatedAction::class.java, any())
            } returns automatedActionMock

            every { instanceValueReadServiceMock.readAllForInstance(instances[0].id) } returns emptyList()

            every { workflowInteractionServiceMock.readEdgesBySourceNodeId(instances[0].currentNode.id) } returns
                emptySet()

            // when
            val exception = shouldThrow<AutomatedActionException> { cut.executeBatch() }

            // then
            exception.message shouldBe
                "The current node with id ${instances[0].currentNode.id} has no edge with the given name $edgeName"
        }
    }
})
