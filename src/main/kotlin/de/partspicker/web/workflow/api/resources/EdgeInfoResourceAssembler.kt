package de.partspicker.web.workflow.api.resources

import de.partspicker.web.common.business.objects.enums.AccessLevel.USE
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.common.hal.LinkListBuilder
import de.partspicker.web.common.hal.RelationName.ADVANCE
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.hal.withRel
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.workflow.api.ProjectWorkflowInteractionController
import de.partspicker.web.workflow.api.requests.AdvanceInstanceStateRequest.Companion.DUMMY
import de.partspicker.web.workflow.business.objects.EdgeInfo
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class EdgeInfoResourceAssembler(
    private val orgUnitAccessService: OrgUnitAccessService
) {
    fun toModel(edgeInfo: EdgeInfo, projectId: Long, orgUnitId: Long): EdgeInfoResource {
        return EdgeInfoResource(
            name = edgeInfo.name,
            displayName = edgeInfo.displayName,
            generateDefaultLinks(edgeInfo.id, projectId, orgUnitId)
        )
    }

    private fun generateDefaultLinks(edgeId: Long, projectId: Long, orgUnitId: Long): List<Link> {
        return LinkListBuilder()
            .with(
                linkTo<ProjectWorkflowInteractionController> { handleAdvanceInstanceState(projectId, edgeId, DUMMY) }
                    .withRel(ADVANCE)
                    .withName(UPDATE),
                this.orgUnitAccessService.atLeast(orgUnitId, USE)
            )
            .build()
    }
}
