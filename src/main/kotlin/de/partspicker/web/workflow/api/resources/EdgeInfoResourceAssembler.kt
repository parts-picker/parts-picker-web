package de.partspicker.web.workflow.api.resources

import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.withName
import de.partspicker.web.workflow.api.WorkflowInteractionController
import de.partspicker.web.workflow.business.objects.EdgeInfo
import org.springframework.hateoas.IanaLinkRelations.COLLECTION
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class EdgeInfoResourceAssembler : RepresentationModelAssembler<EdgeInfo, EdgeInfoResource> {
    override fun toModel(edgeInfo: EdgeInfo): EdgeInfoResource {
        return EdgeInfoResource(
            name = edgeInfo.name,
            displayName = edgeInfo.displayName,
            generateDefaultLinks(edgeInfo.sourceNodeId)
        )
    }

    private fun generateDefaultLinks(nodeId: Long) = listOf(
        linkTo<WorkflowInteractionController> { handleGetAllEdgesForNode(nodeId) }
            .withRel(COLLECTION)
            .withName(READ)
    )
}
