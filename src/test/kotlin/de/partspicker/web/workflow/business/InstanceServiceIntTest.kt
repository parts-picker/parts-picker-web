package de.partspicker.web.workflow.business

import de.partspicker.web.test.annotations.ReducedSpringTestContext
import de.partspicker.web.test.builders.WorkflowCreateBuilder
import de.partspicker.web.test.generators.workflow.NodeCreateGenerators
import de.partspicker.web.test.util.AutomatedTestAction
import de.partspicker.web.test.util.WorkflowTestSetupHelper
import de.partspicker.web.workflow.business.objects.create.nodes.AutomatedActionNodeCreate
import de.partspicker.web.workflow.business.objects.nodes.AutomatedActionNode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.property.arbitrary.single
import org.springframework.context.annotation.Import

@ReducedSpringTestContext
@Import(
    InstanceService::class,
    WorkflowInteractionService::class,
    WorkflowMigrationService::class,
    InstanceValueService::class,
    InstanceValueMigrationService::class,
    SpELConfig::class,
    WorkflowTestSetupHelper::class,
    WorkflowService::class,
    AutomatedTestAction::class,
)
class InstanceServiceIntTest(
    cut: InstanceService,
    // support classes
    workflowTestSetupHelper: WorkflowTestSetupHelper
) : ShouldSpec({

    context("readInstancesWaitingForAutomatedRunner") {
        should("return at max the requested amount of instances with current node being automated") {
            // given
            // create workflow with automated node
            val automatedActionNodeCreate = AutomatedActionNodeCreate(
                name = "automatedAction",
                displayName = "Automated Action",
                automatedActionName = AutomatedTestAction.NAME
            )
            val workflowCreate = WorkflowCreateBuilder(NodeCreateGenerators.startNodeCreateGenerator.single())
                .append(NodeCreateGenerators.userActionNodeCreateGenerator.single())
                .append(automatedActionNodeCreate)
                .build(NodeCreateGenerators.stopNodeCreateGenerator.single())

            val workflow = workflowTestSetupHelper.setupWorkflow(workflowCreate)

            // create instances for the workflow at first node after start node
            repeat(3) {
                workflowTestSetupHelper.setupInstance(workflow)
            }

            // create instances for the workflow at automated node
            val maxHits = 5
            repeat(maxHits + 1) {
                val instance = workflowTestSetupHelper.setupInstance(workflow)
                workflowTestSetupHelper.forceInstanceStatus(instance.id, automatedActionNodeCreate.name)
            }

            // when
            val result = cut.readInstancesWaitingForAutomatedRunner(maxHits)

            // then
            result shouldHaveSize maxHits
            result.forAll {
                it.currentNode is AutomatedActionNode
            }
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
