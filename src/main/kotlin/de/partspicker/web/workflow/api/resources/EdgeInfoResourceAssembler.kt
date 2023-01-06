package de.partspicker.web.workflow.api.resources

import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.common.hal.RelationName.ADVANCE
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.hal.withRel
import de.partspicker.web.workflow.api.WorkflowInteractionController
import de.partspicker.web.workflow.api.requests.AdvanceInstanceStateRequest.Companion.DUMMY
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
            generateDefaultLinks(edgeInfo.id, edgeInfo.instanceId)
        )
    }

    private fun generateDefaultLinks(edgeId: Long, instanceId: Long) = listOf(
        linkTo<WorkflowInteractionController> { handleGetAllEdgesForInstance(instanceId) }
            .withRel(COLLECTION)
            .withName(READ),
        linkTo<WorkflowInteractionController> { handleAdvanceInstanceState(instanceId, edgeId, DUMMY) }
            .withRel(ADVANCE)
            .withName(UPDATE)
    )
}
