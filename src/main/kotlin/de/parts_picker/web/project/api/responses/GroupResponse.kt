package de.parts_picker.web.project.api.responses

import de.parts_picker.web.project.persistance.entities.GroupEntity

data class GroupResponse (
    val id: Long,
    val name: String,
    val description: String?
)

fun GroupEntity.asResponse() = GroupResponse(
    id = id!!,
    name = name!!,
    description = description
)