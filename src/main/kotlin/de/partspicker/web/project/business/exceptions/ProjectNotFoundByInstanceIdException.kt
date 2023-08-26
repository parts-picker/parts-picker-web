package de.partspicker.web.project.business.exceptions

import de.partspicker.web.common.exceptions.EntityNotFoundException

class ProjectNotFoundByInstanceIdException(instanceId: Long) :
    EntityNotFoundException("Project with instance id $instanceId could not be found")
