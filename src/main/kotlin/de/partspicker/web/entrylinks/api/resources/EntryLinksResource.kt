package de.partspicker.web.entrylinks.api.resources

import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.generateGetAllItemTypesLink
import de.partspicker.web.common.hal.generateGetAllItemsLink
import de.partspicker.web.common.hal.generateGetAllProjectsLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.item.api.ItemTypeController
import de.partspicker.web.item.api.requests.ItemTypePostRequest
import de.partspicker.web.project.api.ProjectController
import de.partspicker.web.project.api.requests.ProjectPostRequest
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo

class EntryLinksResource(
    links: Iterable<Link> = emptyList()
) : RepresentationModel<EntryLinksResource>(links) {

    companion object {
        const val ITEMS_RELATION = "items"
        const val ITEM_TYPES_RELATION = "itemTypes"
        const val PROJECTS_RELATION = "projects"
    }

    init {
        this.generateDefaultLinks()
    }

    private fun generateDefaultLinks() {
        this.add(
            listOf(
                generateGetAllItemTypesLink(ITEM_TYPES_RELATION),
                linkTo<ItemTypeController> { handlePostItemType(ItemTypePostRequest.DUMMY) }
                    .withRel(ITEM_TYPES_RELATION)
                    .withName(CREATE),
                generateGetAllItemsLink(ITEMS_RELATION),
                linkTo<ProjectController> { handlePostProject(ProjectPostRequest.DUMMY) }
                    .withRel(PROJECTS_RELATION)
                    .withName(CREATE),
                generateGetAllProjectsLink(PROJECTS_RELATION)
            )
        )
    }
}
