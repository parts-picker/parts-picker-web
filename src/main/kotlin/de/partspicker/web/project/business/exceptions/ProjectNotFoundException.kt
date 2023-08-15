package de.partspicker.web.project.business.exceptions

import de.partspicker.web.common.exceptions.EntityNotFoundException

class ProjectNotFoundException(projectId: Long) :
    EntityNotFoundException("Project with id $projectId could not be found")
