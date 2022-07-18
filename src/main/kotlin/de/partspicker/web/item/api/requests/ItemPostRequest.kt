package de.partspicker.web.item.api.requests

data class ItemPostRequest(
    val typeId: Long,
    val status: ItemStatusRequest,
    val condition: ItemConditionRequest,
    val note: String? = null
) {
    companion object {
        val DUMMY = ItemPostRequest(0L, ItemStatusRequest.IN_STOCK, ItemConditionRequest.UNKNOWN)
    }
}
