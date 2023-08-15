package de.partspicker.web.workflow.business.exceptions.migration

import de.partspicker.web.workflow.business.objects.create.migration.enums.SupportedDataTypeMigrationCreate

class WorkflowMigrationValueHasWrongTypeException(
    value: String,
    expectedType: String
) : WorkflowMigrationException("The given value '$value' cannot be parsed to the expected type '$expectedType'") {
    constructor(
        value: String,
        expectedType: SupportedDataTypeMigrationCreate
    ) : this(value, expectedType.name)
}
