package de.partspicker.web.item.api.requests

data class ItemTypePutRequest(
    val name: String,
    val description: String
) {
    companion object {
        val DUMMY = ItemTypePutRequest("", "")
    }
}
