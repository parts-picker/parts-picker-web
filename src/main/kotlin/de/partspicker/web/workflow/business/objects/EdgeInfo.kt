package de.partspicker.web.workflow.business.objects

import de.partspicker.web.workflow.persistence.entities.EdgeEntity

data class EdgeInfo(
    val id: Long,
    val name: String,
    val displayName: String,
    val sourceNodeId: Long,
    val instanceId: Long
) {
    companion object {
        fun from(edgeEntity: EdgeEntity, instanceId: Long) = EdgeInfo(
            id = edgeEntity.id,
            name = edgeEntity.name,
            displayName = edgeEntity.displayName,
            sourceNodeId = edgeEntity.source.id,
            instanceId = instanceId
        )
    }

    object AsSet {
        fun from(edgeEntities: Iterable<EdgeEntity>, instanceId: Long) =
            edgeEntities.map { from(it, instanceId) }.toSet()
    }
}
