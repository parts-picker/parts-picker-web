package de.partspicker.web.item.api.requests

class ItemGeneralPatchRequest(val condition: ItemConditionRequest, val note: String? = null) : ItemPatchRequest() {
    companion object {
        val DUMMY = ItemGeneralPatchRequest(ItemConditionRequest.UNKNOWN)
    }
}
