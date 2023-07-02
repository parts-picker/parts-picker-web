package de.partspicker.web.inventory.persistence.entities

import de.partspicker.web.inventory.business.objects.CreateOrUpdateRequiredItemType
import de.partspicker.web.inventory.persistence.embeddableids.RequiredItemTypeId
import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import de.partspicker.web.project.persistance.entities.ProjectEntity
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table

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
        fun from(requiredItemType: CreateOrUpdateRequiredItemType) = RequiredItemTypeEntity(
            projectId = requiredItemType.projectId,
            itemTypeId = requiredItemType.itemTypeId,
            requiredAmount = requiredItemType.requiredAmount
        )
    }
}
