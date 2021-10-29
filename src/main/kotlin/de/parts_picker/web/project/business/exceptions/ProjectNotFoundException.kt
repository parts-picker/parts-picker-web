package de.parts_picker.web.project.business.exceptions

class ProjectNotFoundException(projectId: Long) : Exception("Project with id $projectId could not be found")