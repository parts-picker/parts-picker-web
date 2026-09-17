package de.partspicker.web.orgunit.api

import de.partspicker.web.common.security.CurrentUserProvider
import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.orgunit.api.requests.OrgUnitPostRequest
import de.partspicker.web.orgunit.api.resources.OrgUnitMembershipResource
import de.partspicker.web.orgunit.api.resources.OrgUnitMembershipResourceAssembler
import de.partspicker.web.orgunit.api.resources.OrgUnitResource
import de.partspicker.web.orgunit.api.resources.OrgUnitResourceAssembler
import de.partspicker.web.orgunit.business.OrgUnitReadService
import de.partspicker.web.orgunit.business.OrgUnitService
import de.partspicker.web.orgunit.business.objects.CreateOrgUnit
import de.partspicker.web.orgunit.business.objects.OrgUnitMembership
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Suppress("LongParameterList")
@Controller
class OrgUnitController(
    private val orgUnitService: OrgUnitService,
    private val orgUnitReadService: OrgUnitReadService,
    private val currentUserProvider: CurrentUserProvider,
    private val orgUnitResourceAssembler: OrgUnitResourceAssembler,
    private val orgUnitMembershipResourceAssembler: OrgUnitMembershipResourceAssembler,
    private val pagedResourcesAssembler: PagedResourcesAssembler<OrgUnitMembership>
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @PostMapping("/org-units")
    fun handlePostOrgUnit(@RequestBody body: OrgUnitPostRequest): ResponseEntity<OrgUnitResource> {
        logger.info("=> POST request for new org unit")

        val createdOrgUnit = this.orgUnitService.create(
            CreateOrgUnit(
                name = body.name,
                shortDescription = body.shortDescription,
                ownerId = this.currentUserProvider.getCurrentUser().id
            )
        )

        return ResponseEntity(orgUnitResourceAssembler.toModel(createdOrgUnit), HttpStatus.OK)
    }

    @GetMapping("/org-unit-memberships")
    fun handleGetAllMembershipsOfCurrentUser(
        pageable: Pageable
    ): ResponseEntity<PagedModel<OrgUnitMembershipResource>> {
        logger.info("=> GET request for all org unit memberships of the current user")

        val memberships = this.orgUnitReadService.findAllMembershipsOfCurrentUser(pageable)
        val pagedResource = this.pagedResourcesAssembler.toModel(memberships, orgUnitMembershipResourceAssembler)

        return ResponseEntity(pagedResource, HttpStatus.OK)
    }

    @GetMapping("/org-units/{id}")
    fun handleGetOrgUnitById(@PathVariable id: Long): ResponseEntity<OrgUnitResource> {
        logger.info("=> GET request for org unit with id $id")

        val orgUnitResource = orgUnitResourceAssembler.toModel(this.orgUnitReadService.getById(id))

        return ResponseEntity(orgUnitResource, HttpStatus.OK)
    }
}
