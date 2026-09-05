package de.partspicker.web.orgunit.api.resources

import de.partspicker.web.orgunit.business.objects.OrgUnitSummary

data class OrgUnitSummaryResource(
    val name: String,
    val shortDescription: String?
) {
    companion object {
        fun from(orgUnitSummary: OrgUnitSummary) = OrgUnitSummaryResource(
            name = orgUnitSummary.name,
            shortDescription = orgUnitSummary.shortDescription
        )
    }
}
