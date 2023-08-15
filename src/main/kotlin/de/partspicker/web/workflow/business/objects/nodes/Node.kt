package de.partspicker.web.workflow.business.objects.nodes

import de.partspicker.web.workflow.business.exceptions.WorkflowException
import de.partspicker.web.workflow.business.objects.enums.StartType
import de.partspicker.web.workflow.persistence.entities.nodes.NodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.StartNodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.StopNodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.UserActionNodeEntity

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

                is StartNodeEntity -> StartNode(
                    id = nodeEntity.id,
                    workflowId = nodeEntity.workflow.id,
                    name = nodeEntity.name,
                    displayName = nodeEntity.displayName,
                    startType = StartType.from(nodeEntity.startType)
                )

                is StopNodeEntity -> StopNode(
                    id = nodeEntity.id,
                    workflowId = nodeEntity.workflow.id,
                    name = nodeEntity.name,
                    displayName = nodeEntity.displayName
                )

                else -> throw WorkflowException()
            }
        }
    }

    object AsList {
        fun from(nodeEntities: Iterable<NodeEntity>) = nodeEntities.map { from(it) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Node) return false

        if (id != other.id) return false
        if (workflowId != other.workflowId) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + workflowId.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
