package de.partspicker.web.project.persistance.entities

import de.partspicker.web.common.persistence.entities.CreationInfo
import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntity
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed

@Entity
@Indexed
@Table(name = "groups")
data class GroupEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_gen")
    @SequenceGenerator(name = "group_gen", sequenceName = "group_seq", allocationSize = 1)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String? = null,

    @FullTextField
    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_unit_id", foreignKey = ForeignKey(name = "fk_org_unit_of_group"))
    var orgUnit: OrgUnitEntity,

    @Embedded
    var creation: CreationInfo
)
