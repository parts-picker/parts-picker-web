package de.partspicker.web.common.util

import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException

/**
 * The name of the violated constraint or null if no name given.
 */
fun DataIntegrityViolationException.violatedConstraint(): String? =
    (this.cause as? ConstraintViolationException)?.constraintName
