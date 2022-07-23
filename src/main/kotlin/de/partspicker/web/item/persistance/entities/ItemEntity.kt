package de.partspicker.web.item.persistance.entities

import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.persistance.entities.enums.ItemConditionEntity
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "items")
data class ItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_gen")
    @SequenceGenerator(name = "item_gen", sequenceName = "item_seq", allocationSize = 1)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", foreignKey = ForeignKey(name = "fk_type_of_item"))
    val type: ItemTypeEntity,

    @Enumerated(EnumType.STRING)
    var status: ItemStatusEntity,

    @Enumerated(EnumType.STRING)
    var condition: ItemConditionEntity,

    var note: String?
) {
    companion object {
        fun from(item: Item) = ItemEntity(
            id = item.id,
            type = ItemTypeEntity.from(item.type),
            status = ItemStatusEntity.from(item.status),
            condition = ItemConditionEntity.from(item.condition),
            note = item.note
        )
    }
}
