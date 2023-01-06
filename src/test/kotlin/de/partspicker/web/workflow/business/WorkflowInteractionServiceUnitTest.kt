package de.partspicker.web.workflow.business

import de.partspicker.web.test.generators.EdgeEntityGenerators
import de.partspicker.web.test.generators.InstanceEntityGenerators
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotActiveException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.objects.EdgeInfo
import de.partspicker.web.workflow.business.objects.NodeInfo
import de.partspicker.web.workflow.persistance.EdgeRepository
import de.partspicker.web.workflow.persistance.InstanceRepository
import de.partspicker.web.workflow.persistance.NodeRepository
import de.partspicker.web.workflow.persistance.WorkflowRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

class WorkflowInteractionServiceUnitTest : ShouldSpec({

    val workflowRepositoryMock = mockk<WorkflowRepository>()
    val instanceRepositoryMock = mockk<InstanceRepository>()
    val nodeRepositoryMock = mockk<NodeRepository>()
    val edgeRepositoryMock = mockk<EdgeRepository>()
    val instanceValueServiceMock = mockk<InstanceValueService>()

    val cut = WorkflowInteractionService(
        workflowRepository = workflowRepositoryMock,
        instanceRepository = instanceRepositoryMock,
        nodeRepository = nodeRepositoryMock,
        edgeRepository = edgeRepositoryMock,
        instanceValueService = instanceValueServiceMock
    )

    context("read current node info by instance") {
        should("return node info") {
            // given
            val instanceEntity = InstanceEntityGenerators.generator.next()

            every { instanceRepositoryMock.findById(instanceEntity.id) } returns Optional.of(instanceEntity)

            // when
            val returnedNodeInfo = cut.readCurrentNodeByInstanceId(instanceEntity.id)

            // then
            returnedNodeInfo shouldBe NodeInfo.from(instanceEntity.currentNode!!, instanceEntity.id)
        }

        should("return null when instance has no current node assigned") {
            // given
            val instanceEntity = InstanceEntityGenerators.generator.next().copy(currentNode = null)

            every { instanceRepositoryMock.findById(instanceEntity.id) } returns Optional.of(instanceEntity)

            // when
            val returnedNodeInfo = cut.readCurrentNodeByInstanceId(instanceEntity.id)

            // then
            returnedNodeInfo shouldBe null
        }

        should("throw WorkflowInstanceNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(1).next()
            every { instanceRepositoryMock.findById(randomId) } returns Optional.empty()

            // when
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.readCurrentNodeByInstanceId(randomId)
            }

            // then
            exception.message shouldBe "Workflow instance with id $randomId could not be found"
        }
    }

    context("read all possible edges by source node") {
        should("return all edges with the given source node") {
            // given
            val instance = InstanceEntityGenerators.generator.next()
            instance.active = true
            val edgeEntities = listOf(
                EdgeEntityGenerators.generator.next(),
                EdgeEntityGenerators.generator.next()
            )
            every { instanceRepositoryMock.findById(instance.id) } returns Optional.of(instance)
            every { edgeRepositoryMock.findAllBySourceId(instance.currentNode!!.id) } returns edgeEntities

            // when
            val returnedEdgeInfos = cut.readPossibleEdgesByInstanceId(instance.id)

            // then
            returnedEdgeInfos shouldBe EdgeInfo.AsSet.from(edgeEntities, instance.id)
        }

        should("throw InstanceNotFoundException when given non-existent id") {
            // given
            val nonExistentId = Arb.long(1).next()
            every { instanceRepositoryMock.findById(nonExistentId) } returns Optional.empty()

            // when
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.readPossibleEdgesByInstanceId(nonExistentId)
            }

            // then
            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }

        should("throw WorkflowInstanceNotActiveException when given non-existent id") {
            // given
            val instance = InstanceEntityGenerators.generator.next()
            instance.active = false
            every { instanceRepositoryMock.findById(instance.id) } returns Optional.of(instance)

            // when
            val exception = shouldThrow<WorkflowInstanceNotActiveException> {
                cut.readPossibleEdgesByInstanceId(instance.id)
            }

            // then
            exception.message shouldBe "Workflow instance with id ${instance.id} " +
                "cannot be edited because it is inactive."
        }
    }
})
