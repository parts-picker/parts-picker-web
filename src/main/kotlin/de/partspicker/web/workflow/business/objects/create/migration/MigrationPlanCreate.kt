package de.partspicker.web.workflow.business.objects.create.migration

import de.partspicker.web.common.util.duplicates
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.workflow.api.json.WorkflowJson
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationDuplicateSourceNodeException
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationImplicitNodeRuleCreationException
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationSourceNodeNotFoundException
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationTargetNodeNotFoundException
import de.partspicker.web.workflow.business.objects.Workflow

class MigrationPlanCreate private constructor(
    val nodeMigrations: List<NodeMigrationCreate>
) {
    companion object {
        fun from(targetWorkflowJson: WorkflowJson, sourceWorkflow: Workflow): MigrationPlanCreate {
            val migrationPlanJson = targetWorkflowJson.migrationPlan

            // validate existing migration plan json
            val explicitNodeRules: List<NodeMigrationCreate> = migrationPlanJson?.nodeMigrations?.let {
                NodeMigrationCreate.AsList.from(it)
            } ?: emptyList()

            // check if nodes in explicit rules exist
            // source nodes
            val nodeRuleSourceNodeNames = explicitNodeRules.map { it.sourceNode }
            val sourceWorkflowNodeNames = sourceWorkflow.nodes.map { it.name }.toSet()
            val unknownSourceNodes = nodeRuleSourceNodeNames - sourceWorkflowNodeNames
            unknownSourceNodes.isEmpty() elseThrow WorkflowMigrationSourceNodeNotFoundException(unknownSourceNodes)

            // target nodes
            val nodeRuleTargetNodeNames = explicitNodeRules.map { it.targetNode }
            val targetWorkflowNodeNames = targetWorkflowJson.nodes.map { it.name }.toSet()
            val unknownTargetNodes = nodeRuleTargetNodeNames - targetWorkflowNodeNames
            unknownTargetNodes.isEmpty() elseThrow WorkflowMigrationTargetNodeNotFoundException(unknownTargetNodes)

            // check for duplicates in explicit rules source nodes
            val nodeRuleSourceNodeNameDuplicates = nodeRuleSourceNodeNames.duplicates()
            nodeRuleSourceNodeNameDuplicates.isEmpty() elseThrow
                WorkflowMigrationDuplicateSourceNodeException(nodeRuleSourceNodeNameDuplicates)

            // find all nodes without explicit migration rules
            val sourceWorkflowNodes = sourceWorkflow.nodes
            val nodesWithoutRule = sourceWorkflowNodes.filter { !nodeRuleSourceNodeNames.contains(it.name) }

            // create implicit migration rules
            val implicitNodeRules = nodesWithoutRule.map {
                if (!targetWorkflowNodeNames.contains(it.name)) {
                    throw WorkflowMigrationImplicitNodeRuleCreationException(it.name)
                }

                NodeMigrationCreate(
                    it.name,
                    it.name,
                    emptyList()
                )
            }

            return MigrationPlanCreate(
                nodeMigrations = explicitNodeRules + implicitNodeRules
            )
        }
    }
}
