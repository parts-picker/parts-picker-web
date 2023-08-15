package de.partspicker.web.workflow.business

import de.partspicker.web.test.annotations.ReducedSpringTestContext
import de.partspicker.web.test.generators.workflow.WorkflowCreateGenerators
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeSameSizeAs
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import org.springframework.context.annotation.Import

@ReducedSpringTestContext
@Import(
    WorkflowService::class,
    WorkflowMigrationService::class,
    InstanceValueService::class
)
class WorkflowServiceIntTest(
    private val cut: WorkflowService,
) : ShouldSpec({

    context("create") {
        should("create new workflow") {
            // given
            val workflowCreate = WorkflowCreateGenerators.generator.next()

            // when
            val workflow = cut.create(workflowCreate)

            // then
            workflow.name shouldBe workflowCreate.name
            workflow.version shouldBe workflowCreate.version
            workflow.nodes shouldBeSameSizeAs workflowCreate.nodes
            workflow.edges shouldBeSameSizeAs workflowCreate.edges
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
