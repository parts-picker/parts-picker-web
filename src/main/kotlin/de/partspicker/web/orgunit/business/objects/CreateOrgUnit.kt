package de.partspicker.web.orgunit.business.objects

data class CreateOrgUnit(
    val name: String,
    val shortDescription: String? = null,
    val ownerId: Long
)
