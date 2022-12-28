package de.partspicker.web.workflow.api.json

import de.partspicker.web.workflow.api.json.nodes.NodeJson

data class WorkflowJson(
    val name: String,
    val version: Long,

    val nodes: List<NodeJson>,
    val edges: List<EdgeJson>
)
