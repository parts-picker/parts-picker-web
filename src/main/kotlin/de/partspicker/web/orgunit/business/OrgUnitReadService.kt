package de.partspicker.web.orgunit.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.security.CurrentUserProvider
import de.partspicker.web.orgunit.business.exceptions.OrgUnitNotFoundException
import de.partspicker.web.orgunit.business.objects.OrgUnit
import de.partspicker.web.orgunit.business.objects.OrgUnitMembership
import de.partspicker.web.orgunit.persistence.OrgUnitEntitlementRepository
import de.partspicker.web.orgunit.persistence.OrgUnitRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class OrgUnitReadService(
    private val orgUnitRepository: OrgUnitRepository,
    private val orgUnitEntitlementRepository: OrgUnitEntitlementRepository,
    private val orgUnitAccessService: OrgUnitAccessService,
    private val currentUserProvider: CurrentUserProvider
) {

    fun getById(id: Long): OrgUnit {
        this.orgUnitAccessService.requireAtLeast(id, AccessLevel.READ)

        val orgUnitEntity = this.orgUnitRepository.findWithOwnerAndCreatorById(id)
            ?: throw OrgUnitNotFoundException(id)

        return OrgUnit.from(orgUnitEntity)
    }

    /**
     * The memberships of the user of the current request, one per org unit they hold an entitlement in.
     */
    fun findAllMembershipsOfCurrentUser(pageable: Pageable): Page<OrgUnitMembership> =
        this.orgUnitEntitlementRepository
            .findAllWithOrgUnitByUserId(this.currentUserProvider.getCurrentUser().id, pageable)
            .map { entitlement -> OrgUnitMembership.from(entitlement) }
}
