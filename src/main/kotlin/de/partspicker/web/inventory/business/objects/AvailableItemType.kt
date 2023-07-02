package de.partspicker.web.inventory.business.objects

import de.partspicker.web.inventory.persistence.results.AvailableItemTypeResult

data class AvailableItemType(
    val id: Long,
    val name: String,
    /**
     * The id of the project which may use this item type.
     */
    val projectId: Long,
    val projectStatus: String?
) {
    companion object {
        fun from(
            availableItemTypeResult: AvailableItemTypeResult,
            projectId: Long,
            projectStatus: String?
        ) = AvailableItemType(
            id = availableItemTypeResult.id,
            name = availableItemTypeResult.name,
            projectId = projectId,
            projectStatus = projectStatus
        )
    }

    object AsList {
        fun from(availableItemTypeResults: Iterable<AvailableItemTypeResult>, projectId: Long, projectStatus: String?) =
            availableItemTypeResults.map { from(it, projectId, projectStatus) }
    }
}
