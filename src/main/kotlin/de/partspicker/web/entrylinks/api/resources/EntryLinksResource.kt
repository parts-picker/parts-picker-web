package de.partspicker.web.entrylinks.api.resources

import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.generateGetAllItemTypesLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.item.api.ItemController
import de.partspicker.web.item.api.ItemTypeController
import de.partspicker.web.item.api.requests.ItemPostRequest
import de.partspicker.web.item.api.requests.ItemTypePostRequest
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo

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
                linkTo<ItemTypeController> { handlePostItemType(ItemTypePostRequest.DUMMY) }
                    .withRel(ITEM_TYPES_RELATION)
                    .withName(CREATE),
                linkTo<ItemController> { handleGetAllItems() }
                    .withRel(ITEMS_RELATION)
                    .withName(READ),
                linkTo<ItemController> { handlePostItem(ItemPostRequest.DUMMY) }
                    .withRel(ITEMS_RELATION)
                    .withName(CREATE),
                generateGetAllItemTypesLink(ITEM_TYPES_RELATION),
            )
        )
    }
}
