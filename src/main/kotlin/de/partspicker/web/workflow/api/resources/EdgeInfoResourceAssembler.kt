package de.partspicker.web.workflow.api.resources

import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.common.hal.RelationName.ADVANCE
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.hal.withRel
import de.partspicker.web.workflow.api.WorkflowInteractionController
import de.partspicker.web.workflow.api.requests.AdvanceInstanceStateRequest.Companion.DUMMY
import de.partspicker.web.workflow.business.objects.EdgeInfo
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class EdgeInfoResourceAssembler {
    fun toModel(edgeInfo: EdgeInfo, projectId: Long): EdgeInfoResource {
        return EdgeInfoResource(
            name = edgeInfo.name,
            displayName = edgeInfo.displayName,
            generateDefaultLinks(edgeInfo.id, projectId)
        )
    }

    private fun generateDefaultLinks(edgeId: Long, projectId: Long) = listOf(
        linkTo<WorkflowInteractionController> { handleAdvanceInstanceState(projectId, edgeId, DUMMY) }
            .withRel(ADVANCE)
            .withName(UPDATE)
    )
}
