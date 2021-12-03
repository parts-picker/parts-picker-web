package de.partspicker.web.project.business.exceptions

class GroupNotFoundException(groupId: Long) : Exception("Group with id $groupId could not be found")
