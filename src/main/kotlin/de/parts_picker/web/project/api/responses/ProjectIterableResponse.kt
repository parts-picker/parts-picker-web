package de.parts_picker.web.project.api.responses

import de.parts_picker.web.project.persistance.entities.ProjectEntity

data class ProjectIterableResponse (
    val projects: Iterable<ProjectResponse>
)

fun Iterable<ProjectEntity>.asResponse() = ProjectIterableResponse(projects = map(ProjectEntity::asResponse))