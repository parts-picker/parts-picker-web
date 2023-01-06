package de.partspicker.web.workflow.api.resources

import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.RelationName
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.hal.withRel
import de.partspicker.web.workflow.api.WorkflowInteractionController
import de.partspicker.web.workflow.business.objects.NodeInfo
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class NodeInfoResourceAssembler : RepresentationModelAssembler<NodeInfo, NodeInfoResource> {
    override fun toModel(nodeInfo: NodeInfo): NodeInfoResource {
        return NodeInfoResource(
            name = nodeInfo.name,
            displayName = nodeInfo.displayName,
            links = generateDefaultLinks(nodeInfo.instanceId, nodeInfo.userCanInteract)
        )
    }

    private fun generateDefaultLinks(instanceId: Long, userCanInteract: Boolean): List<Link> {
        val links = mutableListOf(
            linkTo<WorkflowInteractionController> { handleGetCurrentNodeInfoForInstance(instanceId) }
                .withSelfRel()
                .withName(READ)
        )

        if (userCanInteract) {
            links.add(
                linkTo<WorkflowInteractionController> { handleGetAllEdgesForInstance(instanceId) }
                    .withRel(RelationName.OPTIONS)
                    .withName(READ)
            )
        }

        return links
    }
}
