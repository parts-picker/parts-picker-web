package de.partspicker.web.orgunit.persistence.entities

import de.partspicker.web.common.persistence.entities.enums.AccessLevelEntity
import de.partspicker.web.user.persistence.entities.UserEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "org_unit_entitlements")
data class OrgUnitEntitlementEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_unit_entitlement_gen")
    @SequenceGenerator(
        name = "org_unit_entitlement_gen",
        sequenceName = "org_unit_entitlement_seq",
        allocationSize = 1
    )
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_unit_id")
    var orgUnit: OrgUnitEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: UserEntity,

    @Enumerated(EnumType.STRING)
    var accessLevel: AccessLevelEntity,

    var joinedOn: Instant
)
