package de.partspicker.web.workflow.business.objects.nodes

import de.partspicker.web.workflow.business.exceptions.WorkflowException
import de.partspicker.web.workflow.business.objects.enums.StartType
import de.partspicker.web.workflow.persistence.entities.nodes.AutomatedActionNodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.NodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.StartNodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.StopNodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.UserActionNodeEntity
import org.hibernate.Hibernate
import org.hibernate.proxy.HibernateProxy

sealed class Node(
    val id: Long,
    val workflowId: Long,
    val name: String,
    val displayName: String
) {
    companion object {
        fun from(nodeEntity: NodeEntity): Node {
            val unproxiedEntity = if (nodeEntity is HibernateProxy) Hibernate.unproxy(nodeEntity) else nodeEntity

            return when (unproxiedEntity) {
                is UserActionNodeEntity -> UserActionNode(
                    id = unproxiedEntity.id,
                    workflowId = unproxiedEntity.workflow.id,
                    name = unproxiedEntity.name,
                    displayName = unproxiedEntity.displayName
                )

                is StartNodeEntity -> StartNode(
                    id = unproxiedEntity.id,
                    workflowId = unproxiedEntity.workflow.id,
                    name = unproxiedEntity.name,
                    displayName = unproxiedEntity.displayName,
                    startType = StartType.from(unproxiedEntity.startType)
                )

                is AutomatedActionNodeEntity -> AutomatedActionNode(
                    id = unproxiedEntity.id,
                    workflowId = unproxiedEntity.workflow.id,
                    name = unproxiedEntity.name,
                    displayName = unproxiedEntity.displayName,
                    automatedActionName = unproxiedEntity.automatedActionName
                )

                is StopNodeEntity -> StopNode(
                    id = unproxiedEntity.id,
                    workflowId = unproxiedEntity.workflow.id,
                    name = unproxiedEntity.name,
                    displayName = unproxiedEntity.displayName
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
