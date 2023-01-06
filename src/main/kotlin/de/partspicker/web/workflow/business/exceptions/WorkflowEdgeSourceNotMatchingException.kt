package de.partspicker.web.workflow.business.exceptions

class WorkflowEdgeSourceNotMatchingException(edgeId: Long, edgeSourceNodeId: Long, nodeId: Long) : RuntimeException(
    "The current instance node with id $nodeId does not match the source node with id $edgeSourceNodeId " +
        "of the given edge with id $edgeId to advance the instance state"
)
