package de.partspicker.web.orgunit.business.exceptions

class OrgUnitNameAlreadyUsedException(name: String, cause: Throwable) :
    RuntimeException("The org unit name '$name' is already used by another org unit of the same owner", cause)
