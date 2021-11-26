package de.parts_picker.web.item.persistance.entities

import de.parts_picker.web.item.persistance.entities.enums.ItemConditionEntity
import de.parts_picker.web.item.persistance.entities.enums.ItemStatusEntity
import javax.persistence.*

@Entity
@Table(name="items")
data class ItemEntity(
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "item_gen")
    @SequenceGenerator(name = "item_gen", sequenceName = "item_seq", allocationSize = 1)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="type_id", foreignKey = ForeignKey(name = "fk_type_of_item"))
    val type: ItemTypeEntity,

    @Enumerated(EnumType.STRING)
    val status: ItemStatusEntity,

    @Enumerated(EnumType.STRING)
    val condition: ItemConditionEntity,

    val note: String?
)
