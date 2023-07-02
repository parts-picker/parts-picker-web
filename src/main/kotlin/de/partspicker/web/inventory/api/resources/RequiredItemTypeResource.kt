package de.partspicker.web.inventory.api.resources

import de.partspicker.web.inventory.api.resources.RequiredItemTypeResource.Companion.collectionRelationName
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = collectionRelationName)
class RequiredItemTypeResource(
    val itemTypeName: String,
    val assignedAmount: Long,
    val requiredAmount: Long,
    links: Iterable<Link> = emptyList()
) : RepresentationModel<RequiredItemTypeResource>(links) {
    companion object {
        const val collectionRelationName = "requiredItemTypes"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RequiredItemTypeResource) return false
        if (!super.equals(other)) return false

        if (itemTypeName != other.itemTypeName) return false
        if (assignedAmount != other.assignedAmount) return false
        if (requiredAmount != other.requiredAmount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + itemTypeName.hashCode()
        result = 31 * result + assignedAmount.hashCode()
        result = 31 * result + requiredAmount.hashCode()
        return result
    }

    override fun toString(): String {
        return "RequiredItemTypeResource(itemTypeName='$itemTypeName', " +
            "assignedAmount=$assignedAmount, requiredAmount=$requiredAmount)"
    }
}
