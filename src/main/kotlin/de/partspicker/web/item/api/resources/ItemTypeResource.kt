package de.partspicker.web.item.api.resources

import de.partspicker.web.common.hal.withMethods
import de.partspicker.web.item.api.ItemTypeController
import de.partspicker.web.item.business.objects.ItemType
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpMethod

@Relation(collectionRelation = ItemTypeResource.collectionRelationName)
class ItemTypeResource(
    val name: String,
    val description: String,
    links: Iterable<Link>
) : RepresentationModel<ItemTypeResource>(links) {
    companion object {
        const val collectionRelationName = "itemTypes"

        fun from(itemType: ItemType, vararg links: Link): ItemTypeResource {
            val combinedLinks = generateDefaultLinks(itemTypeId = itemType.id!!) + links

            return ItemTypeResource(
                name = itemType.name,
                description = itemType.description,
                links = combinedLinks
            )
        }

        private fun generateDefaultLinks(itemTypeId: Long): List<Link> {
            return listOf(
                linkTo<ItemTypeController> { handleGetItemTypeById(itemTypeId) }.withSelfRel()
                    .withMethods(HttpMethod.GET),
                linkTo<ItemTypeController> { handleGetAllItemTypes() }.withRel(IanaLinkRelations.COLLECTION)
                    .withMethods(HttpMethod.GET)
            )
        }
    }

    object AsList {
        fun from(itemTypes: Iterable<ItemType>) = itemTypes.map { from(it) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ItemTypeResource

        if (name != other.name) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        return result
    }

    override fun toString(): String {
        return "ItemTypeResource(name='$name', description='$description')"
    }
}
