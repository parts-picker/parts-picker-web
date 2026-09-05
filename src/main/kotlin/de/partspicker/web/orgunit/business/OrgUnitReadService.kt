package de.partspicker.web.orgunit.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.orgunit.business.exceptions.OrgUnitNotFoundException
import de.partspicker.web.orgunit.business.objects.OrgUnit
import de.partspicker.web.orgunit.business.objects.OrgUnitSummary
import de.partspicker.web.orgunit.persistence.OrgUnitEntitlementRepository
import de.partspicker.web.orgunit.persistence.OrgUnitRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class OrgUnitReadService(
    private val orgUnitRepository: OrgUnitRepository,
    private val orgUnitEntitlementRepository: OrgUnitEntitlementRepository,
    private val orgUnitAccessService: OrgUnitAccessService
) {

    fun getById(id: Long): OrgUnit {
        this.orgUnitAccessService.requireAtLeast(id, AccessLevel.READ)

        val orgUnitEntity = this.orgUnitRepository.findWithOwnerAndCreatorById(id)
            ?: throw OrgUnitNotFoundException(id)

        return OrgUnit.from(orgUnitEntity)
    }

    /**
     * Summaries of the org units the user of the current request holds an entitlement in.
     */
    fun findAllOfCurrentUser(pageable: Pageable): Page<OrgUnitSummary> =
        this.orgUnitEntitlementRepository
            .findAllWithOrgUnitByUserId(this.orgUnitAccessService.currentUser().id, pageable)
            .map { entitlement -> OrgUnitSummary.from(entitlement.orgUnit) }
}
