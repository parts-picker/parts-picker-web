package de.partspicker.web.item.api.requests

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes(
    Type(value = ItemProjectPutRequest::class, name = "ItemProjectPutRequest"),
    Type(value = ItemGeneralPutRequest::class, name = "ItemGeneralPutRequest")
)
// must be a sealed class, so that when() can be exhausted without an else-branch
sealed class ItemPutRequest
