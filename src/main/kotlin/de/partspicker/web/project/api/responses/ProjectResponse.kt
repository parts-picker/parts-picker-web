package de.partspicker.web.project.api.responses

import de.partspicker.web.project.persistance.entities.ProjectEntity

data class ProjectResponse(
    val id: Long,
    val name: String,
    val description: String?,
    var groupId: Long?,
)

fun ProjectEntity.asResponse() = ProjectResponse(
    id = id!!,
    name = name,
    description = description,
    groupId = group?.id,
)
