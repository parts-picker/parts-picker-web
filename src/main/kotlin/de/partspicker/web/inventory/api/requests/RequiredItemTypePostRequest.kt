package de.partspicker.web.inventory.api.requests

import javax.validation.constraints.Min

data class RequiredItemTypePostRequest(
    @field:Min(1)
    val itemTypeId: Long,

    @field:Min(1)
    val requiredAmount: Long
) {
    companion object {
        val DUMMY = RequiredItemTypePostRequest(0, 1)
    }
}
