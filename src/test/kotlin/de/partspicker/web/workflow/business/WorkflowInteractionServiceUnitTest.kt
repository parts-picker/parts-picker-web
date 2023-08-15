package de.partspicker.web.workflow.business

import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.test.generators.workflow.EdgeEntityGenerators
import de.partspicker.web.test.generators.workflow.InstanceEntityGenerators
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.persistence.EdgeRepository
import de.partspicker.web.workflow.persistence.InstanceRepository
import de.partspicker.web.workflow.persistence.NodeRepository
import de.partspicker.web.workflow.persistence.WorkflowRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

class WorkflowInteractionServiceUnitTest : ShouldSpec({

    val workflowRepositoryMock = mockk<WorkflowRepository>()
    val instanceRepositoryMock = mockk<InstanceRepository>()
    val nodeRepositoryMock = mockk<NodeRepository>()
    val edgeRepositoryMock = mockk<EdgeRepository>()
    val instanceValueServiceMock = mockk<InstanceValueService>()
    val projectRepositoryMock = mockk<ProjectRepository>()

    val cut = WorkflowInteractionService(
        workflowRepository = workflowRepositoryMock,
        instanceRepository = instanceRepositoryMock,
        nodeRepository = nodeRepositoryMock,
        edgeRepository = edgeRepositoryMock,
        instanceValueService = instanceValueServiceMock,
        projectRepository = projectRepositoryMock
    )

    context("read current instance info") {
        should("return instance info") {
            // given
            val instanceEntity = InstanceEntityGenerators.generator.next()
            val options = listOf(
                EdgeEntityGenerators.generator.single(),
                EdgeEntityGenerators.generator.single()
            )

            every { instanceRepositoryMock.findById(instanceEntity.id) } returns Optional.of(instanceEntity)
            every { edgeRepositoryMock.findAllBySourceId(instanceEntity.currentNode!!.id) } returns options

            // when
            val returnedNodeInfo = cut.readInstanceInfo(instanceEntity.id)!!

            // then
            returnedNodeInfo.nodeId shouldBe instanceEntity.currentNode!!.id
            returnedNodeInfo.name shouldBe instanceEntity.currentNode!!.name
            returnedNodeInfo.instanceId shouldBe instanceEntity.id
            returnedNodeInfo.options shouldHaveSize options.size
        }

        should("return null when instance has no current node assigned") {
            // given
            val instanceEntity = InstanceEntityGenerators.generator.next().copy(currentNode = null)

            every { instanceRepositoryMock.findById(instanceEntity.id) } returns Optional.of(instanceEntity)

            // when
            val returnedNodeInfo = cut.readInstanceInfo(instanceEntity.id)

            // then
            returnedNodeInfo shouldBe null
        }

        should("throw WorkflowInstanceNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(1).next()
            every { instanceRepositoryMock.findById(randomId) } returns Optional.empty()

            // when
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.readInstanceInfo(randomId)
            }

            // then
            exception.message shouldBe "Workflow instance with id $randomId could not be found"
        }
    }
})
