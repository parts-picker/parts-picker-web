package de.partspicker.web.inventory.business.objects

import de.partspicker.web.inventory.persistence.results.AvailableItemTypeResult

data class AvailableItemType(
    val id: Long,
    val name: String,
    /**
     * The id of the project which may use this item type.
     */
    val projectId: Long,
    val projectStatus: String,
    val orgUnitId: Long
) {
    companion object {
        fun from(
            availableItemTypeResult: AvailableItemTypeResult,
            projectId: Long,
            projectStatus: String,
            orgUnitId: Long
        ) = AvailableItemType(
            id = availableItemTypeResult.id,
            name = availableItemTypeResult.name,
            projectId = projectId,
            projectStatus = projectStatus,
            orgUnitId = orgUnitId
        )
    }

    object AsList {
        fun from(
            availableItemTypeResults: Iterable<AvailableItemTypeResult>,
            projectId: Long,
            projectStatus: String,
            orgUnitId: Long
        ) = availableItemTypeResults.map { from(it, projectId, projectStatus, orgUnitId) }
    }
}
