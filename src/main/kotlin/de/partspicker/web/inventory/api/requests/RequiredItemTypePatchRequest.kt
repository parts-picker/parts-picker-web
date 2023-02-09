package de.partspicker.web.inventory.api.requests

import jakarta.validation.constraints.Min

data class RequiredItemTypePatchRequest(
    @field:Min(1)
    val requiredAmount: Long
) {
    companion object {
        val DUMMY = RequiredItemTypePatchRequest(1)
    }
}
