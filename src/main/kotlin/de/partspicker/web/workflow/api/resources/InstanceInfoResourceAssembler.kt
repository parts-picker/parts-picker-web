package de.partspicker.web.workflow.api.resources

import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.withName
import de.partspicker.web.workflow.api.ProjectWorkflowInteractionController
import de.partspicker.web.workflow.api.resources.enums.DisplayTypeInfoResource
import de.partspicker.web.workflow.business.objects.ProjectInstanceInfo
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class InstanceInfoResourceAssembler(
    private val edgeInfoResourceAssembler: EdgeInfoResourceAssembler
) : RepresentationModelAssembler<ProjectInstanceInfo, InstanceInfoResource> {
    override fun toModel(projectInstanceInfo: ProjectInstanceInfo): InstanceInfoResource {
        val instanceInfo = projectInstanceInfo.instanceInfo

        return InstanceInfoResource(
            name = instanceInfo.name,
            displayName = instanceInfo.displayName,
            options = instanceInfo.options.map {
                edgeInfoResourceAssembler.toModel(it, projectInstanceInfo.projectId, projectInstanceInfo.orgUnitId)
            },
            message = instanceInfo.message,
            displayType = DisplayTypeInfoResource.from(instanceInfo.displayType),
            links = generateDefaultLinks(projectInstanceInfo.projectId)
        )
    }

    private fun generateDefaultLinks(projectId: Long): List<Link> {
        return listOf(
            linkTo<ProjectWorkflowInteractionController> { handleGetInstanceInfo(projectId) }
                .withSelfRel()
                .withName(READ)
        )
    }
}
