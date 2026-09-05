package de.partspicker.web.entrylinks.api.resources

import de.partspicker.web.common.hal.generateGetAllOrgUnitsOfCurrentUserLink
import de.partspicker.web.common.hal.generatePostOrgUnitLink
import de.partspicker.web.orgunit.api.resources.OrgUnitResource
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel

/**
 * The entry point of the api. For normal users, an org unit is the base for most following requests.
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
                generatePostOrgUnitLink(OrgUnitResource.COLLECTION_RELATION_NAME)
            )
        )
    }
}
