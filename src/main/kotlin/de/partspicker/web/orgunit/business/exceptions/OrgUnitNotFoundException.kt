package de.partspicker.web.orgunit.business.exceptions

import de.partspicker.web.common.exceptions.EntityNotFoundException

class OrgUnitNotFoundException(orgUnitId: Long) :
    EntityNotFoundException("OrgUnit with id $orgUnitId could not be found")
