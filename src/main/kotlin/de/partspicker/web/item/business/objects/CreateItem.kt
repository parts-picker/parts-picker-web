package de.partspicker.web.item.business.objects

import de.partspicker.web.item.api.requests.ItemPostRequest
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.business.objects.enums.ItemStatus

data class CreateItem(
    val itemTypeId: Long,
    val assignedProjectId: Long?,
    val status: ItemStatus,
    val condition: ItemCondition,
    val note: String?
) {
    companion object {
        fun from(itemPostRequest: ItemPostRequest, itemTypeId: Long) = CreateItem(
            itemTypeId = itemTypeId,
            assignedProjectId = itemPostRequest.assignedProjectId,
            status = ItemStatus.from(itemPostRequest.status),
            condition = ItemCondition.from(itemPostRequest.condition),
            note = itemPostRequest.note
        )
    }
}
