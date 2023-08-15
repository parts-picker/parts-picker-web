package de.partspicker.web.workflow.business.objects.create.migration

import de.partspicker.web.common.util.duplicates
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.workflow.api.json.migration.NodeMigrationJson
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationDuplicatedInstanceValueKeyException
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationIllegalArgumentException

data class NodeMigrationCreate(
    val sourceNode: String,
    val targetNode: String,
    val instanceValueMigrations: List<InstanceValueMigrationCreate>
) {
    companion object {
        const val SOURCE_NODE_IS_EMPTY = "Source node must be a non empty string"
        const val TARGET_NODE_IS_EMPTY = "Target node must be a non empty string"

        fun from(nodeMigrationJson: NodeMigrationJson) = NodeMigrationCreate(
            sourceNode = nodeMigrationJson.sourceNode,
            targetNode = nodeMigrationJson.targetNode,
            instanceValueMigrations = InstanceValueMigrationCreate.AsList.from(
                nodeMigrationJson.instanceValueMigrations
            )
        )
    }

    object AsList {
        fun from(nodeMigrationJsons: List<NodeMigrationJson>) = nodeMigrationJsons.map { from(it) }
    }

    init {
        sourceNode.isNotBlank() elseThrow WorkflowMigrationIllegalArgumentException(SOURCE_NODE_IS_EMPTY)
        targetNode.isNotBlank() elseThrow WorkflowMigrationIllegalArgumentException(TARGET_NODE_IS_EMPTY)

        val instanceValueMigrationDuplicates = this.instanceValueMigrations.map { it.key }.duplicates()
        instanceValueMigrationDuplicates.isEmpty() elseThrow
            WorkflowMigrationDuplicatedInstanceValueKeyException(
                instanceValueMigrationDuplicates,
                sourceNode,
                targetNode
            )
    }
}
