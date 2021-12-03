package de.partspicker.web.project.api.responses

import de.partspicker.web.project.persistance.entities.ProjectEntity

data class ProjectIterableResponse(
    val projects: Iterable<ProjectResponse>
)

fun Iterable<ProjectEntity>.asResponse() = ProjectIterableResponse(projects = map(ProjectEntity::asResponse))
