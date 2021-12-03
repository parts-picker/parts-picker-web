package de.partspicker.web.project.api.responses

import de.partspicker.web.project.persistance.entities.GroupEntity

data class GroupResponse(
    val id: Long,
    val name: String,
    val description: String?
)

fun GroupEntity.asResponse() = GroupResponse(
    id = id!!,
    name = name!!,
    description = description
)
