package de.parts_picker.web.project.api.requests

import de.parts_picker.web.project.persistance.entities.GroupEntity
import de.parts_picker.web.project.persistance.entities.ProjectEntity

data class PostProjectRequest (
    val name: String,
    val description: String?,
    var groupId: Long?,
)

fun PostProjectRequest.asEntity() = ProjectEntity(
    id = null,
    name = name,
    description = description,
    group = if (groupId != null) GroupEntity(id = groupId) else null
)