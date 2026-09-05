package de.partspicker.web.orgunit.api.resources

import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.withName
import de.partspicker.web.orgunit.api.OrgUnitController
import de.partspicker.web.orgunit.business.objects.OrgUnitSummary
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class OrgUnitSummaryResourceAssembler : RepresentationModelAssembler<OrgUnitSummary, OrgUnitSummaryResource> {
    override fun toModel(orgUnitSummary: OrgUnitSummary): OrgUnitSummaryResource {
        return OrgUnitSummaryResource(
            name = orgUnitSummary.name,
            shortDescription = orgUnitSummary.shortDescription,
            links = generateDefaultLinks(orgUnitSummary.id)
        )
    }

    private fun generateDefaultLinks(orgUnitId: Long): List<Link> {
        return listOf(
            linkTo<OrgUnitController> { handleGetOrgUnitById(orgUnitId) }
                .withSelfRel()
                .withName(READ)
        )
    }
}
