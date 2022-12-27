package de.partspicker.web.workflow.business.objects

import de.partspicker.web.workflow.persistance.entities.EdgeEntity

data class EdgeInfo(
    val name: String,
    val displayName: String,
    val sourceNodeId: Long
) {
    companion object {
        fun from(edgeEntity: EdgeEntity) = EdgeInfo(
            name = edgeEntity.name,
            displayName = edgeEntity.displayName,
            sourceNodeId = edgeEntity.source.id
        )
    }

    object AsSet {
        fun from(edgeEntities: Iterable<EdgeEntity>) = edgeEntities.map { from(it) }.toSet()
    }
}
