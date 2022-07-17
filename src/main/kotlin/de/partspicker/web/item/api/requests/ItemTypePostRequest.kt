package de.partspicker.web.item.api.requests

data class ItemTypePostRequest(
    val name: String,
    val description: String
) {
    companion object {
        val DUMMY = ItemTypePostRequest("", "")
    }
}
