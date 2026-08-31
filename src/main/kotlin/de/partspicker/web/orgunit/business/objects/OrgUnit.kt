package de.partspicker.web.orgunit.business.objects

import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntity
import de.partspicker.web.user.business.objects.User

data class OrgUnit(
    val id: Long,
    val name: String,
    val shortDescription: String?,
    val owner: User
) {
    companion object {
        fun from(orgUnitEntity: OrgUnitEntity) = OrgUnit(
            id = orgUnitEntity.id,
            name = orgUnitEntity.name,
            shortDescription = orgUnitEntity.shortDescription,
            owner = User.from(orgUnitEntity.owner)
        )
    }
}
