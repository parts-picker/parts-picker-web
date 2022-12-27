package de.partspicker.web.workflow.business.objects.nodes

import de.partspicker.web.workflow.business.exceptions.WorkflowException
import de.partspicker.web.workflow.persistance.entities.nodes.NodeEntity
import de.partspicker.web.workflow.persistance.entities.nodes.UserActionNodeEntity

sealed class Node(
    val id: Long,
    val workflowId: Long,
    val name: String
) {
    companion object {
        fun from(nodeEntity: NodeEntity): Node {
            return when (nodeEntity) {
                is UserActionNodeEntity -> UserActionNode(
                    id = nodeEntity.id,
                    workflowId = nodeEntity.workflow.id,
                    name = nodeEntity.name,
                    displayName = nodeEntity.displayName
                )

                else -> throw WorkflowException()
            }
        }
    }
}
