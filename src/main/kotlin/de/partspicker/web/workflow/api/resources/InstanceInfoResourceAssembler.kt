package de.partspicker.web.workflow.api.resources

import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.withName
import de.partspicker.web.workflow.api.WorkflowInteractionController
import de.partspicker.web.workflow.api.resources.enums.DisplayTypeInfoResource
import de.partspicker.web.workflow.business.objects.InstanceInfo
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class InstanceInfoResourceAssembler(
    private val edgeInfoResourceAssembler: EdgeInfoResourceAssembler
) {
    fun toModel(instanceInfo: InstanceInfo, projectId: Long): InstanceInfoResource {
        return InstanceInfoResource(
            name = instanceInfo.name,
            displayName = instanceInfo.displayName,
            options = instanceInfo.options.map { edgeInfoResourceAssembler.toModel(it, projectId) },
            message = instanceInfo.message,
            displayType = DisplayTypeInfoResource.from(instanceInfo.displayType),
            links = generateDefaultLinks(projectId)
        )
    }

    private fun generateDefaultLinks(projectId: Long): List<Link> {
        return mutableListOf(
            linkTo<WorkflowInteractionController> { handleGetInstanceInfo(projectId) }
                .withSelfRel()
                .withName(READ)
        )
    }
}
