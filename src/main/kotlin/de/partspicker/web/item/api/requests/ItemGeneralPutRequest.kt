package de.partspicker.web.item.api.requests

class ItemGeneralPutRequest(val condition: ItemConditionRequest, val note: String? = null) : ItemPutRequest() {
    companion object {
        val DUMMY = ItemGeneralPutRequest(ItemConditionRequest.UNKNOWN)
    }
}
