package de.parts_picker.web.project.api.requests

import de.parts_picker.web.project.persistance.entities.GroupEntity

data class GroupRequest (
    val name: String,
    val description: String?
)

fun GroupRequest.asEntity() = GroupEntity(
    id = null,
    name = name,
    description = description
)