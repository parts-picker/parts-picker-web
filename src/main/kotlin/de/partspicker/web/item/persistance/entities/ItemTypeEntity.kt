package de.partspicker.web.item.persistance.entities

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
    val id: Long? = null,

    val name: String,

    val description: String
)
