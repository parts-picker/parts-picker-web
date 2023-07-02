package de.partspicker.web.inventory.api.resources

import de.partspicker.web.inventory.api.responses.InventoryItemConditionResponse
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = AssignedItemResource.collectionRelationName)
class AssignedItemResource(
    val condition: InventoryItemConditionResponse,
    links: Iterable<Link> = emptyList()
) : RepresentationModel<AssignedItemResource>(links) {
    companion object {
        const val collectionRelationName = "assignedItems"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as AssignedItemResource

        if (condition != other.condition) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + condition.hashCode()
        return result
    }

    override fun toString(): String {
        return "AssignedItemResource(condition=$condition)"
    }
}
