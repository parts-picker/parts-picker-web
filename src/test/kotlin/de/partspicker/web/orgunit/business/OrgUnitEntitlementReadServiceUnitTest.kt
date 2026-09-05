package de.partspicker.web.orgunit.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.persistence.entities.enums.AccessLevelEntity
import de.partspicker.web.orgunit.persistence.OrgUnitEntitlementRepository
import de.partspicker.web.test.generators.OrgUnitEntitlementEntityGenerators
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class OrgUnitEntitlementReadServiceUnitTest : ShouldSpec({
    val orgUnitEntitlementRepositoryMock = mockk<OrgUnitEntitlementRepository>()
    val cut = OrgUnitEntitlementReadService(
        orgUnitEntitlementRepository = orgUnitEntitlementRepositoryMock
    )

    afterTest {
        clearMocks(orgUnitEntitlementRepositoryMock)
    }

    context("accessLevelOf") {
        should("return the level the given user holds within the given org unit") {
            // given
            val entitlementEntity = OrgUnitEntitlementEntityGenerators.generator.next()
                .copy(accessLevel = AccessLevelEntity.MAINTAIN)
            every {
                orgUnitEntitlementRepositoryMock.findByOrgUnitIdAndUserId(
                    entitlementEntity.orgUnit.id,
                    entitlementEntity.user.id
                )
            } returns entitlementEntity

            // when
            val accessLevel = cut.accessLevelOf(entitlementEntity.orgUnit.id, entitlementEntity.user.id)

            // then
            accessLevel shouldBe AccessLevel.MAINTAIN
        }

        should("return none when the given user is no member of the given org unit") {
            // given
            every { orgUnitEntitlementRepositoryMock.findByOrgUnitIdAndUserId(any(), any()) } returns null

            // when & then
            cut.accessLevelOf(1L, 2L) shouldBe AccessLevel.NONE
            verify(exactly = 1) { orgUnitEntitlementRepositoryMock.findByOrgUnitIdAndUserId(1L, 2L) }
        }
    }
})
