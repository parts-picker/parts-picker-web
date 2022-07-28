package de.partspicker.web.item.api.requests

data class ItemPostRequest(
    val status: ItemStatusRequest,
    val condition: ItemConditionRequest,
    val note: String? = null
) {
    companion object {
        val DUMMY = ItemPostRequest(ItemStatusRequest.IN_STOCK, ItemConditionRequest.UNKNOWN)
    }
}
