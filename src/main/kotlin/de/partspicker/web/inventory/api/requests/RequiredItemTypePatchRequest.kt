package de.partspicker.web.inventory.api.requests

import javax.validation.constraints.Min

data class RequiredItemTypePatchRequest(
    @field:Min(1)
    val requiredAmount: Long
)
