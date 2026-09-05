package de.partspicker.web.orgunit.business.exceptions

import de.partspicker.web.common.business.exceptions.RuleException
import de.partspicker.web.common.business.objects.enums.AccessLevel

class OrgUnitAccessDeniedException(orgUnitId: Long, requiredLevel: AccessLevel) :
    RuleException("This action requires at least $requiredLevel in org unit with id $orgUnitId")
