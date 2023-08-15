package de.partspicker.web.workflow.business.objects

import de.partspicker.web.workflow.persistence.entities.EdgeEntity

data class Edge(
    val id: Long,
    val workflowId: Long,
    val sourceNodeId: Long,
    val targetNodeId: Long,
    val name: String,
    val displayName: String
) {
    companion object {
        fun from(edgeEntity: EdgeEntity) = Edge(
            id = edgeEntity.id,
            workflowId = edgeEntity.workflow.id,
            sourceNodeId = edgeEntity.source.id,
            targetNodeId = edgeEntity.target.id,
            name = edgeEntity.name,
            displayName = edgeEntity.displayName
        )
    }

    object AsList {
        fun from(edgeEntities: List<EdgeEntity>) = edgeEntities.map { from(it) }
    }
}
