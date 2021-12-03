package de.partspicker.web.project.api.requests

import de.partspicker.web.project.persistance.entities.GroupEntity
import de.partspicker.web.project.persistance.entities.ProjectEntity

data class PutProjectRequest(
    val name: String,
    val description: String?,
    var groupId: Long?,
)

fun PutProjectRequest.asEntity(id: Long) = ProjectEntity(
    id = id,
    name = name,
    description = description,
    group = if (groupId != null) GroupEntity(id = groupId) else null
)
