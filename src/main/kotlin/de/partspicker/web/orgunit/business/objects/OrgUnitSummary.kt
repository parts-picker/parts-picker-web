package de.partspicker.web.orgunit.business.objects

import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntity

/**
 * A smaller summary of an org unit.
 */
data class OrgUnitSummary(
    val id: Long,
    val name: String,
    val shortDescription: String?
) {
    companion object {
        fun from(orgUnitEntity: OrgUnitEntity) = OrgUnitSummary(
            id = orgUnitEntity.id,
            name = orgUnitEntity.name,
            shortDescription = orgUnitEntity.shortDescription
        )
    }
}
