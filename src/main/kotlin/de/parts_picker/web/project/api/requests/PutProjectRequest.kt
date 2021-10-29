package de.parts_picker.web.project.api.requests

import de.parts_picker.web.project.persistance.entities.GroupEntity
import de.parts_picker.web.project.persistance.entities.ProjectEntity

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