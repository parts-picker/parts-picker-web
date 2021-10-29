package de.parts_picker.web.project.api.requests

import de.parts_picker.web.project.persistance.entities.GroupEntity

data class PutGroupRequest (
    val name: String,
    val description: String?
)

fun PutGroupRequest.asEntity(id: Long) = GroupEntity(
    id = id,
    name = name,
    description = description
)