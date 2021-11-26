package de.parts_picker.web.item.persistance.entities

import javax.persistence.*

@Entity
@Table(name="item_types")
data class ItemTypeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_type_gen")
    @SequenceGenerator(name = "item_type_gen", sequenceName = "item_type_seq", allocationSize = 1)
    val id: Long? = null,

    val name: String,

    val description: String
)