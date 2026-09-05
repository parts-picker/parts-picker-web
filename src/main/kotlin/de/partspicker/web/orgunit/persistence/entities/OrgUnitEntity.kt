package de.partspicker.web.orgunit.persistence.entities

import de.partspicker.web.common.persistence.entities.CreationInfo
import de.partspicker.web.user.persistence.entities.UserEntity
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField

@Entity
@Table(name = "org_units")
data class OrgUnitEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_unit_gen")
    @SequenceGenerator(name = "org_unit_gen", sequenceName = "org_unit_seq", allocationSize = 1)
    @GenericField
    var id: Long = 0,

    var name: String,

    var shortDescription: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    var owner: UserEntity,

    @Embedded
    var creation: CreationInfo
)
