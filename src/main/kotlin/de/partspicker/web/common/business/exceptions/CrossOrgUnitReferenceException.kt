package de.partspicker.web.common.business.exceptions

class CrossOrgUnitReferenceException(firstOrgUnitId: Long, secondOrgUnitId: Long) :
    RuleException(
        "Rows of org unit $firstOrgUnitId & org unit $secondOrgUnitId cannot be linked to each other"
    )
