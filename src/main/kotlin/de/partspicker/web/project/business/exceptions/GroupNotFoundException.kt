package de.partspicker.web.project.business.exceptions

import de.partspicker.web.common.exceptions.EntityNotFoundException

class GroupNotFoundException(groupId: Long) : EntityNotFoundException("Group with id $groupId could not be found")
