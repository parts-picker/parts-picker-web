package de.partspicker.web.workflow.business

import de.partspicker.web.test.generators.EdgeEntityGenerators
import de.partspicker.web.test.generators.InstanceEntityGenerators
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.objects.EdgeInfo
import de.partspicker.web.workflow.business.objects.NodeInfo
import de.partspicker.web.workflow.persistance.EdgeRepository
import de.partspicker.web.workflow.persistance.InstanceRepository
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

    val instanceRepositoryMock = mockk<InstanceRepository>()
    val edgeRepositoryMock = mockk<EdgeRepository>()
    val cut = WorkflowInteractionService(
        instanceRepository = instanceRepositoryMock,
        edgeRepository = edgeRepositoryMock
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

        should("throw InstanceNotFoundException when given non-existent id") {
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
            val nodeId = 1L
            val edgeEntities = listOf(
                EdgeEntityGenerators.generator.next(),
                EdgeEntityGenerators.generator.next()
            )
            every { edgeRepositoryMock.findAllBySourceId(nodeId) } returns edgeEntities

            // when
            val returnedEdgeInfos = cut.readPossibleEdgesByNodeId(nodeId)

            // then
            returnedEdgeInfos shouldBe EdgeInfo.AsSet.from(edgeEntities)
        }
    }
})
