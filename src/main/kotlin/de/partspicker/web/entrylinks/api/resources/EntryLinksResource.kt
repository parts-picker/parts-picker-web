package de.partspicker.web.entrylinks.api.resources

import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.generateGetAllOrgUnitsOfCurrentUserLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.orgunit.api.OrgUnitController
import de.partspicker.web.orgunit.api.requests.OrgUnitPostRequest
import de.partspicker.web.orgunit.api.resources.OrgUnitResource
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo

/**
 * The entry point of the api. Everything a user can reach hangs off an org unit, so this offers their
 * org units & the means to make another one.
 */
class EntryLinksResource(
    links: Iterable<Link> = emptyList()
) : RepresentationModel<EntryLinksResource>(links) {

    init {
        this.generateDefaultLinks()
    }

    private fun generateDefaultLinks() {
        this.add(
            listOf(
                generateGetAllOrgUnitsOfCurrentUserLink(OrgUnitResource.COLLECTION_RELATION_NAME),
                linkTo<OrgUnitController> { handlePostOrgUnit(OrgUnitPostRequest.DUMMY) }
                    .withRel(OrgUnitResource.COLLECTION_RELATION_NAME)
                    .withName(CREATE)
            )
        )
    }
}
