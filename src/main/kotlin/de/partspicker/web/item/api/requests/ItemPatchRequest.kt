package de.partspicker.web.item.api.requests

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes(
    Type(value = ItemGeneralPatchRequest::class, name = "ItemGeneralPatchRequest")
)
// must be a sealed class, so that when() can be exhausted without an else-branch
sealed class ItemPatchRequest
