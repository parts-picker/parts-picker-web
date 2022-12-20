package de.partspicker.web.project.api.requests

import de.partspicker.web.project.persistance.entities.GroupEntity

data class PostGroupRequest(
    val name: String,
    val description: String?
)

fun PostGroupRequest.asEntity() = GroupEntity(
    id = 0,
    name = name,
    description = description
)
