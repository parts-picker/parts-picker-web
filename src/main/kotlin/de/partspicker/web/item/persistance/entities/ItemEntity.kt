package de.partspicker.web.item.persistance.entities

import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.persistance.entities.enums.ItemConditionEntity
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity
import de.partspicker.web.project.persistance.entities.ProjectEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_project_id", foreignKey = ForeignKey(name = "fk_assigned_project"))
    var assignedProject: ProjectEntity?,

    @Enumerated(EnumType.STRING)
    var status: ItemStatusEntity,

    @Enumerated(EnumType.STRING)
    var condition: ItemConditionEntity,

    var note: String?
) {
    companion object {
        fun from(item: Item, projectEntity: ProjectEntity?) = ItemEntity(
            id = item.id,
            assignedProject = projectEntity,
            type = ItemTypeEntity.from(item.type),
            status = ItemStatusEntity.from(item.status),
            condition = ItemConditionEntity.from(item.condition),
            note = item.note
        )
    }
}
