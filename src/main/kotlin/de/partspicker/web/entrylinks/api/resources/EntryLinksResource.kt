package de.partspicker.web.entrylinks.api.resources

import de.partspicker.web.common.hal.withMethods
import de.partspicker.web.item.api.ItemController
import de.partspicker.web.item.api.ItemTypeController
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpMethod

class EntryLinksResource(
    links: Iterable<Link> = emptyList()
) : RepresentationModel<EntryLinksResource>(links) {

    companion object {
        const val ITEMS_RELATION = "items"
        const val ITEM_TYPES_RELATION = "itemTypes"
    }

    init {
        this.generateDefaultLinks()
    }

    private fun generateDefaultLinks() {
        this.add(
            listOf(
                linkTo<ItemController> { handleGetAllItems() }.withRel(ITEMS_RELATION)
                    .withMethods(HttpMethod.GET),
                linkTo<ItemTypeController> { handleGetAllItemTypes() }.withRel(ITEM_TYPES_RELATION)
                    .withMethods(HttpMethod.GET, HttpMethod.POST)
            )
        )
    }
}
