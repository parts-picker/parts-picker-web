package de.partspicker.web.workflow.business.objects.nodes

class UserActionNode(
    id: Long,
    workflowId: Long,
    name: String,
    val displayName: String
) : Node(id, workflowId, name)
