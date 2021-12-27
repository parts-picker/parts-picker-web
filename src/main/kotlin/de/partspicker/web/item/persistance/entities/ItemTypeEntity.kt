package de.partspicker.web.item.persistance.entities

import de.partspicker.web.item.business.objects.ItemType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "item_types")
data class ItemTypeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_type_gen")
    @SequenceGenerator(name = "item_type_gen", sequenceName = "item_type_seq", allocationSize = 1)
    val id: Long = 0,

    val name: String,

    val description: String
) {
    companion object {
        fun from(itemType: ItemType) = ItemTypeEntity(
            id = itemType.id,
            name = itemType.name,
            description = itemType.description
        )
    }
}
