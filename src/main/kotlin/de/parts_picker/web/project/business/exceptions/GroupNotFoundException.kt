package de.parts_picker.web.project.business.exceptions

class GroupNotFoundException(groupId: Long): Exception("Group with id $groupId could not be found")