package de.partspicker.web.test.util

import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import java.sql.SQLException

/**
 * Builds the exceptions Spring throws when a constraint is violated, as they arrive from Hibernate.
 */
object ConstraintViolations {

    fun of(constraintName: String) = DataIntegrityViolationException(
        constraintName,
        ConstraintViolationException(constraintName, SQLException(constraintName), constraintName)
    )
}
