package de.partspicker.web.item.api.requests

data class ItemPutRequest(
    val status: ItemStatusRequest,
    val condition: ItemConditionRequest,
    val note: String? = null
) {
    companion object {
        val DUMMY = ItemPutRequest(ItemStatusRequest.IN_STOCK, ItemConditionRequest.UNKNOWN)
    }
}
