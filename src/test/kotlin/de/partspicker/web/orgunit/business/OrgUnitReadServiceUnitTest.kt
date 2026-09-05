package de.partspicker.web.orgunit.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.orgunit.business.exceptions.OrgUnitAccessDeniedException
import de.partspicker.web.orgunit.business.exceptions.OrgUnitNotFoundException
import de.partspicker.web.orgunit.persistence.OrgUnitEntitlementRepository
import de.partspicker.web.orgunit.persistence.OrgUnitRepository
import de.partspicker.web.test.generators.OrgUnitEntitlementEntityGenerators
import de.partspicker.web.test.generators.OrgUnitEntityGenerators
import de.partspicker.web.test.generators.UserEntityGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class OrgUnitReadServiceUnitTest : ShouldSpec({

    val orgUnitRepositoryMock = mockk<OrgUnitRepository>()
    val orgUnitEntitlementRepositoryMock = mockk<OrgUnitEntitlementRepository>()
    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val cut = OrgUnitReadService(
        orgUnitRepository = orgUnitRepositoryMock,
        orgUnitEntitlementRepository = orgUnitEntitlementRepositoryMock,
        orgUnitAccessService = orgUnitAccessServiceMock
    )

    afterTest {
        clearMocks(orgUnitRepositoryMock, orgUnitEntitlementRepositoryMock, orgUnitAccessServiceMock)
    }

    context("getById") {
        should("return the org unit with the given id") {
            // given
            val ownerEntity = UserEntityGenerators.humanGenerator.next()
            val orgUnitEntity = OrgUnitEntityGenerators.generatorFor(ownerEntity).next()
            every { orgUnitAccessServiceMock.requireAtLeast(orgUnitEntity.id, AccessLevel.READ) } returns Unit
            every { orgUnitRepositoryMock.findWithOwnerAndCreatorById(orgUnitEntity.id) } returns orgUnitEntity

            // when
            val returnedOrgUnit = cut.getById(orgUnitEntity.id)

            // then
            returnedOrgUnit.id shouldBe orgUnitEntity.id
            returnedOrgUnit.shortDescription shouldBe orgUnitEntity.shortDescription
        }

        should("throw OrgUnitNotFoundException when no org unit with the given id exists") {
            // given
            every { orgUnitAccessServiceMock.requireAtLeast(404L, AccessLevel.READ) } returns Unit
            every { orgUnitRepositoryMock.findWithOwnerAndCreatorById(any()) } returns null

            // when & then
            shouldThrow<OrgUnitNotFoundException> {
                cut.getById(404L)
            }
        }

        should("throw OrgUnitAccessDeniedException without reading when the caller holds nothing") {
            // given
            every {
                orgUnitAccessServiceMock.requireAtLeast(404L, AccessLevel.READ)
            } throws OrgUnitAccessDeniedException(404L, AccessLevel.READ)

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> {
                cut.getById(404L)
            }

            verify(exactly = 0) {
                orgUnitRepositoryMock.findWithOwnerAndCreatorById(any())
            }
        }
    }

    context("findAllOfCurrentUser") {
        should("return a summary of every org unit the current user holds an entitlement in") {
            // given
            val currentUser = UserEntityGenerators.humanGenerator.next()
            val entitlementEntity = OrgUnitEntitlementEntityGenerators.generator.next()
            every { orgUnitAccessServiceMock.currentUser() } returns currentUser
            every {
                orgUnitEntitlementRepositoryMock.findAllWithOrgUnitByUserId(currentUser.id, Pageable.unpaged())
            } returns PageImpl(listOf(entitlementEntity))

            // when
            val returnedOrgUnits = cut.findAllOfCurrentUser(Pageable.unpaged())

            // then
            returnedOrgUnits.map { it.id } shouldContainExactly listOf(entitlementEntity.orgUnit.id)
        }

        should("return an empty list when the current user holds no entitlement at all") {
            // given
            val currentUser = UserEntityGenerators.humanGenerator.next()
            every { orgUnitAccessServiceMock.currentUser() } returns currentUser
            every {
                orgUnitEntitlementRepositoryMock.findAllWithOrgUnitByUserId(currentUser.id, Pageable.unpaged())
            } returns Page.empty()

            // when & then
            cut.findAllOfCurrentUser(Pageable.unpaged()).content shouldBe emptyList()
        }
    }
})
