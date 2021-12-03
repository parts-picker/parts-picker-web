package de.partspicker.web.project.api.requests

import de.partspicker.web.project.persistance.entities.GroupEntity

data class PutGroupRequest(
    val name: String,
    val description: String?
)

fun PutGroupRequest.asEntity(id: Long) = GroupEntity(
    id = id,
    name = name,
    description = description
)
