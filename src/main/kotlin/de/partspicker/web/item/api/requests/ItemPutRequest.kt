package de.partspicker.web.item.api.requests

data class ItemPutRequest(
    val condition: ItemConditionRequest,
    val note: String? = null
) {
    companion object {
        val DUMMY = ItemPutRequest(ItemConditionRequest.UNKNOWN)
    }
}
