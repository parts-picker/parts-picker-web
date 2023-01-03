package de.partspicker.web.workflow.business.objects

import de.partspicker.web.workflow.business.objects.nodes.Node
import de.partspicker.web.workflow.persistance.entities.InstanceEntity

data class Instance(
    val id: Long,
    val currentNode: Node?,
    val active: Boolean
) {
    companion object {
        fun from(instanceEntity: InstanceEntity) = Instance(
            id = instanceEntity.id,
            currentNode = instanceEntity.currentNode?.let { Node.from(it) },
            active = instanceEntity.active
        )
    }
}
