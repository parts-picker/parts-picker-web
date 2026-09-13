package de.partspicker.web.user.business.exceptions

import de.partspicker.web.common.exceptions.EntityNotFoundException

class UserNotFoundException(userId: Long) : EntityNotFoundException("User with id $userId could not be found")
