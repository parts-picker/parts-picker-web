package de.parts_picker.web.item.persistance.entities

import javax.persistence.*

@Entity
@Table(name="item_types")
data class ItemTypeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ITEM_TYPE_SEQ")
    val id: Long? = null,

    val name: String,

    val description: String
)