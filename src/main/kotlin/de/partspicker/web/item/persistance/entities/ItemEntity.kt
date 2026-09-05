package de.partspicker.web.item.persistance.entities

import de.partspicker.web.common.persistence.entities.CreationInfo
import de.partspicker.web.item.persistance.entities.enums.ItemConditionEntity
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity
import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntity
import de.partspicker.web.project.persistance.entities.ProjectEntity
import jakarta.persistence.Embedded
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

    var note: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_unit_id", foreignKey = ForeignKey(name = "fk_org_unit_of_item"))
    var orgUnit: OrgUnitEntity,

    @Embedded
    var creation: CreationInfo
)
