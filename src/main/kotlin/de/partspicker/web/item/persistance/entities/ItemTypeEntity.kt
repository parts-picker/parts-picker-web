package de.partspicker.web.item.persistance.entities

import de.partspicker.web.item.business.objects.ItemType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed

@Entity
@Indexed
@Table(name = "item_types")
data class ItemTypeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_type_gen")
    @SequenceGenerator(name = "item_type_gen", sequenceName = "item_type_seq", allocationSize = 1)
    val id: Long = 0,

    @FullTextField
    val name: String? = null,

    val description: String? = null
) {
    companion object {
        fun from(itemType: ItemType) = ItemTypeEntity(
            id = itemType.id,
            name = itemType.name,
            description = itemType.description
        )
    }
}
