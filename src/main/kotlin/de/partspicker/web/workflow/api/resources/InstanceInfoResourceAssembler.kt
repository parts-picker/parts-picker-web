package de.partspicker.web.workflow.api.resources

import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.withName
import de.partspicker.web.workflow.api.WorkflowInteractionController
import de.partspicker.web.workflow.business.objects.InstanceInfo
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class InstanceInfoResourceAssembler(
    private val edgeInfoResourceAssembler: EdgeInfoResourceAssembler
) : RepresentationModelAssembler<InstanceInfo, InstanceInfoResource> {
    override fun toModel(instanceInfo: InstanceInfo): InstanceInfoResource {
        return InstanceInfoResource(
            name = instanceInfo.name,
            displayName = instanceInfo.displayName,
            options = instanceInfo.options.map { edgeInfoResourceAssembler.toModel(it) },
            links = generateDefaultLinks(instanceInfo.instanceId)
        )
    }

    private fun generateDefaultLinks(instanceId: Long): List<Link> {
        return mutableListOf(
            linkTo<WorkflowInteractionController> { handleGetInstanceInfo(instanceId) }
                .withSelfRel()
                .withName(READ)
        )
    }
}
