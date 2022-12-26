package de.partspicker.web.inventory.persitance.entities

import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.inventory.persitance.embeddableids.RequiredItemTypeId
import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import de.partspicker.web.project.persistance.entities.ProjectEntity
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.Table

@Entity
@Table(name = "required_item_types")
data class RequiredItemTypeEntity(

    @EmbeddedId
    val id: RequiredItemTypeId,

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    val project: ProjectEntity,

    @ManyToOne
    @MapsId("itemTypeId")
    @JoinColumn(name = "item_type_id", referencedColumnName = "id")
    val itemType: ItemTypeEntity,

    @Column(nullable = false)
    val requiredAmount: Long
) {
    constructor(projectId: Long, itemTypeId: Long, requiredAmount: Long) : this(
        id = RequiredItemTypeId(projectId = projectId, itemTypeId = itemTypeId),
        project = ProjectEntity(id = projectId),
        itemType = ItemTypeEntity(id = itemTypeId),
        requiredAmount = requiredAmount
    )

    companion object {
        fun from(requiredItemType: RequiredItemType) = RequiredItemTypeEntity(
            projectId = requiredItemType.projectId,
            itemTypeId = requiredItemType.itemType.id,
            requiredAmount = requiredItemType.requiredAmount
        )
    }
}
