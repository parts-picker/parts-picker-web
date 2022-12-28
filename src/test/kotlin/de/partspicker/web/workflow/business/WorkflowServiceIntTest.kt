package de.partspicker.web.workflow.business

import de.partspicker.web.test.generators.workflow.WorkflowCreateGenerators
import de.partspicker.web.workflow.persistance.EdgeRepository
import de.partspicker.web.workflow.persistance.NodeRepository
import de.partspicker.web.workflow.persistance.WorkflowRepository
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeSameSizeAs
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("integration")
@Transactional
class WorkflowServiceIntTest(
    private val cut: WorkflowService,
    private val workflowRepository: WorkflowRepository,
    private val nodeRepository: NodeRepository,
    private val edgeRepository: EdgeRepository
) : ShouldSpec({

    context("create") {
        should("create new workflow") {
            // given
            val workflowCreate = WorkflowCreateGenerators.generator.next()

            // when
            cut.create(workflowCreate)

            // then
            val newWorkflow = workflowRepository.findByNameAndVersion(workflowCreate.name, workflowCreate.version).get()
            val nodes = nodeRepository.findAllByWorkflowId(newWorkflow.id)
            val edges = edgeRepository.findAllByWorkflowId(newWorkflow.id)

            newWorkflow.name shouldBe workflowCreate.name
            newWorkflow.version shouldBe workflowCreate.version
            nodes shouldBeSameSizeAs workflowCreate.nodes
            edges shouldBeSameSizeAs workflowCreate.edges
        }
    }
})
