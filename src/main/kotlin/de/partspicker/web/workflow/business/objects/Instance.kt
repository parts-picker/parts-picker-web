package de.partspicker.web.workflow.business.objects

import de.partspicker.web.workflow.business.objects.nodes.Node
import de.partspicker.web.workflow.persistence.entities.InstanceEntity

data class Instance(
    val id: Long,
    val currentNode: Node?,
    val active: Boolean,
    val workflowId: Long
) {
    companion object {
        fun from(instanceEntity: InstanceEntity) = Instance(
            id = instanceEntity.id,
            currentNode = Node.from(instanceEntity.currentNode),
            active = instanceEntity.active,
            workflowId = instanceEntity.workflow!!.id
        )

        fun fromOrNull(instanceEntity: InstanceEntity?): Instance? {
            instanceEntity ?: return null

            return from(instanceEntity)
        }
    }

    object AsList {
        fun from(instanceEntities: Iterable<InstanceEntity>) = instanceEntities.map { from(it) }
    }
}
