package de.partspicker.web.workflow.business.objects.create

import de.partspicker.web.workflow.api.json.WorkflowJson
import de.partspicker.web.workflow.business.objects.create.nodes.NodeCreate

data class WorkflowCreate(
    val name: String,
    val version: Long,

    val nodes: List<NodeCreate>,
    val edges: List<EdgeCreate>
) {
    init {
        require(name.isNotBlank())
        require(version > 0)

        // future semantic validation here
    }

    companion object {
        fun from(workflowJson: WorkflowJson) = WorkflowCreate(
            name = workflowJson.name,
            version = workflowJson.version,
            nodes = NodeCreate.AsList.from(workflowJson.nodes),
            edges = EdgeCreate.AsList.from(workflowJson.edges)
        )
    }
}
