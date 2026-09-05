package de.partspicker.web.orgunit.business.objects

import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntity
import de.partspicker.web.user.business.objects.User
import java.time.Instant

data class OrgUnit(
    val id: Long,
    val name: String,
    val shortDescription: String?,
    val owner: User,
    val createdBy: User,
    val createdOn: Instant
) {
    companion object {
        fun from(orgUnitEntity: OrgUnitEntity) = OrgUnit(
            id = orgUnitEntity.id,
            name = orgUnitEntity.name,
            shortDescription = orgUnitEntity.shortDescription,
            owner = User.from(orgUnitEntity.owner),
            createdBy = User.from(orgUnitEntity.creation.createdBy),
            createdOn = orgUnitEntity.creation.createdOn
        )
    }
}
