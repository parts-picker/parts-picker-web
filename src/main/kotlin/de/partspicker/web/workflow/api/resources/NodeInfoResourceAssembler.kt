package de.partspicker.web.workflow.api.resources

import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.withName
import de.partspicker.web.workflow.api.WorkflowInteractionController
import de.partspicker.web.workflow.business.objects.NodeInfo
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class NodeInfoResourceAssembler : RepresentationModelAssembler<NodeInfo, NodeInfoResource?> {
    override fun toModel(nodeInfo: NodeInfo): NodeInfoResource {
        return NodeInfoResource(
            name = nodeInfo.name,
            displayName = nodeInfo.displayName,
            links = generateDefaultLinks(nodeInfo.instanceId)
        )
    }

    private fun generateDefaultLinks(instanceId: Long) = listOf(
        linkTo<WorkflowInteractionController> { handleGetCurrentNodeInfoForInstance(instanceId) }
            .withSelfRel()
            .withName(READ)
    )
}
