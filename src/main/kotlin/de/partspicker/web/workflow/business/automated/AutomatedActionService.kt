package de.partspicker.web.workflow.business.automated

import de.partspicker.web.workflow.business.InstanceService
import de.partspicker.web.workflow.business.InstanceValueReadService
import de.partspicker.web.workflow.business.WorkflowInteractionService
import de.partspicker.web.workflow.business.automated.actions.AutomatedAction
import de.partspicker.web.workflow.business.automated.exceptions.AutomatedActionException
import de.partspicker.web.workflow.business.objects.nodes.AutomatedActionNode
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AutomatedActionService(
    private val instanceService: InstanceService,
    private val applicationContext: ApplicationContext,
    private val workflowInteractionService: WorkflowInteractionService,
    private val instanceValueReadService: InstanceValueReadService
) {
    companion object {
        const val MAX_AUTOMATED_INSTANCE_HITS = 10
    }

    @Transactional
    @Suppress("LabeledExpression")
    suspend fun executeBatch(): Int {
        val instancesToExecute = this.instanceService.readInstancesWaitingForAutomatedRunner(
            MAX_AUTOMATED_INSTANCE_HITS
        )

        var amountExecuted = 0

        instancesToExecute.forEach { instance ->
            if (!instance.active) {
                return@forEach
            }

            val currentNode = instance.currentNode as? AutomatedActionNode
                ?: throw AutomatedActionException(
                    "The instance with id ${instance.id} a non-automated node cannot be executed automatically"
                )

            val automatedAction = this.applicationContext.getBean(
                currentNode.automatedActionName,
                AutomatedAction::class.java
            )

            val instanceValues = this.instanceValueReadService.readAllForInstance(instance.id)
            val automatedActionResult = automatedAction.execute(instance, instanceValues)

            val chosenEdge = this.workflowInteractionService.readEdgesBySourceNodeId(currentNode.id)
                .find { edge -> edge.name == automatedActionResult.chosenEdgeName }
                ?: throw AutomatedActionException(
                    "The current node with id ${currentNode.id} has no edge " +
                        "with the given name ${automatedActionResult.chosenEdgeName}"
                )

            this.workflowInteractionService.advanceInstanceNodeBySystem(
                instanceId = instance.id,
                edgeId = chosenEdge.id,
                values = automatedActionResult.modifiedInstanceValues,
                message = automatedActionResult.message,
                displayType = automatedActionResult.displayType
            )

            amountExecuted += 1
        }

        return amountExecuted
    }
}
