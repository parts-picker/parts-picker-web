package de.partspicker.web.orgunit.api.requests

data class OrgUnitPostRequest(
    val name: String,
    val shortDescription: String?
) {
    companion object {
        val DUMMY = OrgUnitPostRequest(name = "", shortDescription = null)
    }
}
