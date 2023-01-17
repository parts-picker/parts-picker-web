package de.partspicker.web.workflow.business.objects.create

import de.partspicker.web.common.util.duplicates
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.common.util.intersects
import de.partspicker.web.workflow.api.json.WorkflowJson
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeDuplicateException
import de.partspicker.web.workflow.business.exceptions.WorkflowIllegalStateException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeDuplicateException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeHasMoreThanOneTargetException
import de.partspicker.web.workflow.business.exceptions.WorkflowRouteDuplicateException
import de.partspicker.web.workflow.business.exceptions.WorkflowSemanticException
import de.partspicker.web.workflow.business.objects.create.nodes.NodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.StartNodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.StopNodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.UserActionNodeCreate

data class WorkflowCreate(
    val name: String,
    val version: Long,

    val nodes: List<NodeCreate>,
    val edges: List<EdgeCreate>
) {
    init {
        name.isNotBlank() elseThrow WorkflowIllegalStateException(NAME_IS_BLANK)
        (version > 0) elseThrow WorkflowIllegalStateException(VERSION_SMALLER_AS_ONE)

        // semantic validation
        val startNodes = nodes.filterIsInstance<StartNodeCreate>().map { it.name }
        val stopNodes = nodes.filterIsInstance<StopNodeCreate>().map { it.name }

        val sourceNodes = edges.map { it.sourceNode }
        val targetNodes = edges.map { it.targetNode }

        // at least one start node
        startNodes.isNotEmpty() elseThrow WorkflowIllegalStateException(AT_LEAST_ONE_START_NODE)

        // at least one stop node
        stopNodes.isNotEmpty() elseThrow WorkflowIllegalStateException(AT_LEAST_ONE_STOP_NODE)

        // start nodes have no predecessors
        !startNodes.intersects(targetNodes) elseThrow WorkflowIllegalStateException(START_NODE_IS_TARGET)

        // stop nodes have no successors
        !stopNodes.intersects(sourceNodes) elseThrow WorkflowIllegalStateException(STOP_NODE_IS_SOURCE)

        // no duplicate node names
        val allNodes = nodes.map { it.name }
        val duplicatedNodes = allNodes.duplicates()
        duplicatedNodes.isEmpty() elseThrow WorkflowNodeDuplicateException(duplicatedNodes)

        // no duplicate edge names
        val duplicatedEdges = edges.map { it.name }.duplicates()
        duplicatedEdges.isEmpty() elseThrow WorkflowEdgeDuplicateException(duplicatedEdges)

        // no duplicate source -> target combinations
        val duplicatedRoutes = edges.map { Pair(it.sourceNode, it.targetNode) }.duplicates()
        val edgesWithDuplicatedRoutes = edges.filter { duplicatedRoutes.contains(it.sourceNode to it.targetNode) }
            .map { it.name }.toSet()
        duplicatedRoutes.isEmpty() elseThrow WorkflowRouteDuplicateException(edgesWithDuplicatedRoutes)

        // only one edge per node except for user_action nodes
        val nodesToCheckForMultipleEdges = nodes.filter { it !is UserActionNodeCreate }.map { it.name }
        val nodesWithMultipleTargets = edges
            .groupBy { it.sourceNode }
            .filter { nodesToCheckForMultipleEdges.contains(it.key) }
            .filter { it.value.size != 1 }
            .mapValues { entry -> entry.value.map { it.name } }

        nodesWithMultipleTargets.isEmpty() elseThrow WorkflowNodeHasMoreThanOneTargetException(nodesWithMultipleTargets)

        traverseAllRoutes(startNodes, stopNodes, edges, allNodes)
    }

    companion object {
        fun from(workflowJson: WorkflowJson) = WorkflowCreate(
            name = workflowJson.name,
            version = workflowJson.version,
            nodes = NodeCreate.AsList.from(workflowJson.nodes),
            edges = EdgeCreate.AsList.from(workflowJson.edges)
        )

        // validation error messages
        const val NAME_IS_BLANK = "Workflow name cannot be blank"
        const val VERSION_SMALLER_AS_ONE = "Workflow version cannot be smaller than one"
        const val AT_LEAST_ONE_START_NODE = "Workflow needs at least one start node"
        const val AT_LEAST_ONE_STOP_NODE = "Workflow needs at least one stop node"
        const val START_NODE_IS_TARGET = "A start node cannot be the target of an edge"
        const val STOP_NODE_IS_SOURCE = "A stop node cannot be the source of an edge"
    }

    private fun traverseAllRoutes(
        startNodes: List<String>,
        stopNodes: List<String>,
        edgesToCheck: List<EdgeCreate>,
        allNodes: List<String>
    ) {
        // traverse the graph
        val allRoutes = edgesToCheck.groupBy({ it.sourceNode }, { it.targetNode })
        val unvisitedNodes = startNodes.toMutableList()
        val exploredNodes = mutableSetOf<String>()

        while (unvisitedNodes.isNotEmpty()) {
            // pick next node to visit from unvisitedNodes
            val node = unvisitedNodes.first()

            // check if the node has successors
            val targetNodes = allRoutes[node]
            if (targetNodes == null) {
                // only a stop node can have no successors
                if (!stopNodes.contains(node)) {
                    throw WorkflowSemanticException(
                        "Node with name $node is not a stop node & is not the source if any edges"
                    )
                }
            } else {
                // add all nodes to the unvisited nodes, that are not already explored
                unvisitedNodes.addAll(targetNodes - exploredNodes)
            }

            exploredNodes.add(node)
            unvisitedNodes.remove(node)
        }

        if (allNodes.size != exploredNodes.size) {
            val lostNodes = allNodes - exploredNodes
            throw WorkflowSemanticException(
                "Node(s) with name(s): $lostNodes are not start nodes" +
                    " & have no continuous connection from a start node to a stop node"
            )
        }
    }
}
