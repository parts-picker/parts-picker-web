package de.partspicker.web.orgunit.business

import de.partspicker.web.common.persistence.entities.enums.AccessLevelEntity
import de.partspicker.web.orgunit.business.exceptions.OrgUnitNotFoundException
import de.partspicker.web.orgunit.business.objects.CreateOrgUnit
import de.partspicker.web.orgunit.persistence.OrgUnitEntitlementRepository
import de.partspicker.web.orgunit.persistence.OrgUnitRepository
import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntitlementEntity
import de.partspicker.web.test.generators.OrgUnitEntityGenerators
import de.partspicker.web.test.generators.UserEntityGenerators
import de.partspicker.web.test.util.TestConstants.CRUD_REPOSITORY_EXTENSIONS
import de.partspicker.web.user.business.exceptions.UserNotFoundException
import de.partspicker.web.user.persistence.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import org.springframework.data.repository.findByIdOrNull
import java.time.Instant

class OrgUnitServiceUnitTest : ShouldSpec({
    val orgUnitRepositoryMock = mockk<OrgUnitRepository>()
    val orgUnitEntitlementRepositoryMock = mockk<OrgUnitEntitlementRepository>()
    val userRepositoryMock = mockk<UserRepository>()
    val cut = OrgUnitService(
        orgUnitRepository = orgUnitRepositoryMock,
        orgUnitEntitlementRepository = orgUnitEntitlementRepositoryMock,
        userRepository = userRepositoryMock
    )

    beforeSpec {
        mockkStatic(CRUD_REPOSITORY_EXTENSIONS)
    }

    afterSpec {
        unmockkStatic(CRUD_REPOSITORY_EXTENSIONS)
    }

    afterTest {
        clearMocks(orgUnitRepositoryMock, orgUnitEntitlementRepositoryMock, userRepositoryMock)
    }

    context("create") {
        should("store the org unit & entitle its owner to maintain it") {
            // given
            val ownerEntity = UserEntityGenerators.humanGenerator.next()
            val orgUnitEntity = OrgUnitEntityGenerators.generatorFor(ownerEntity).next()
            every { userRepositoryMock.findByIdOrNull(ownerEntity.id) } returns ownerEntity
            every { orgUnitRepositoryMock.save(any()) } returns orgUnitEntity
            val entitlementSlot = slot<OrgUnitEntitlementEntity>()
            every { orgUnitEntitlementRepositoryMock.save(capture(entitlementSlot)) } returns
                OrgUnitEntitlementEntity(
                    orgUnit = orgUnitEntity,
                    user = ownerEntity,
                    accessLevel = AccessLevelEntity.MAINTAIN,
                    joinedOn = Instant.now()
                )

            // when
            val returnedOrgUnit = cut.create(
                CreateOrgUnit(
                    name = orgUnitEntity.name,
                    shortDescription = orgUnitEntity.shortDescription,
                    ownerId = ownerEntity.id
                )
            )

            // then
            returnedOrgUnit.name shouldBe orgUnitEntity.name
            returnedOrgUnit.owner.id shouldBe ownerEntity.id
            entitlementSlot.captured.orgUnit shouldBe orgUnitEntity
            entitlementSlot.captured.user shouldBe ownerEntity
            entitlementSlot.captured.accessLevel shouldBe AccessLevelEntity.MAINTAIN
        }

        should("throw UserNotFoundException when no user with the given owner id exists") {
            // given
            every { userRepositoryMock.findByIdOrNull(any()) } returns null

            // when & then
            shouldThrow<UserNotFoundException> {
                cut.create(CreateOrgUnit(name = "some org unit", ownerId = 404L))
            }
        }
    }

    context("findById") {
        should("return the org unit with the given id") {
            // given
            val ownerEntity = UserEntityGenerators.humanGenerator.next()
            val orgUnitEntity = OrgUnitEntityGenerators.generatorFor(ownerEntity).next()
            every { orgUnitRepositoryMock.findWithOwnerById(orgUnitEntity.id) } returns orgUnitEntity

            // when
            val returnedOrgUnit = cut.findById(orgUnitEntity.id)

            // then
            returnedOrgUnit.id shouldBe orgUnitEntity.id
            returnedOrgUnit.shortDescription shouldBe orgUnitEntity.shortDescription
        }

        should("throw OrgUnitNotFoundException when no org unit with the given id exists") {
            // given
            every { orgUnitRepositoryMock.findWithOwnerById(any()) } returns null

            // when & then
            shouldThrow<OrgUnitNotFoundException> {
                cut.findById(404L)
            }
        }
    }
})
