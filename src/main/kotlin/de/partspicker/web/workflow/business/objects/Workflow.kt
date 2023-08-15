package de.partspicker.web.workflow.business.objects

import de.partspicker.web.workflow.business.objects.nodes.Node
import de.partspicker.web.workflow.persistence.entities.EdgeEntity
import de.partspicker.web.workflow.persistence.entities.WorkflowEntity
import de.partspicker.web.workflow.persistence.entities.nodes.NodeEntity

data class Workflow(
    val id: Long,
    val name: String,
    val version: Long,

    val nodes: List<Node>,
    val edges: List<Edge>,
) {
    companion object {
        fun from(
            workflowEntity: WorkflowEntity,
            nodeEntities: List<NodeEntity>,
            edgeEntities: List<EdgeEntity>
        ) = Workflow(
            id = workflowEntity.id,
            name = workflowEntity.name,
            version = workflowEntity.version,
            nodes = Node.AsList.from(nodeEntities),
            edges = Edge.AsList.from(edgeEntities)
        )
    }
}
