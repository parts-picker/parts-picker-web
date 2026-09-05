package de.partspicker.web.user.business

import de.partspicker.web.orgunit.business.OrgUnitService
import de.partspicker.web.orgunit.business.objects.CreateOrgUnit
import de.partspicker.web.test.generators.OrgUnitGenerators
import de.partspicker.web.test.generators.UserEntityGenerators
import de.partspicker.web.test.generators.UserIdentityGenerators
import de.partspicker.web.user.business.exceptions.UserAlreadyProvisionedException
import de.partspicker.web.user.business.objects.enums.UserType
import de.partspicker.web.user.persistence.UserRepository
import de.partspicker.web.user.persistence.entities.UserEntity
import de.partspicker.web.user.persistence.entities.enums.UserTypeEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.dao.DataIntegrityViolationException

class UserServiceUnitTest : ShouldSpec({
    val userRepositoryMock = mockk<UserRepository>()
    val orgUnitServiceMock = mockk<OrgUnitService>()
    val cut = UserService(userRepository = userRepositoryMock, orgUnitService = orgUnitServiceMock)

    beforeTest {
        every { orgUnitServiceMock.create(any()) } returns OrgUnitGenerators.generator.next()
    }

    afterTest {
        clearMocks(userRepositoryMock, orgUnitServiceMock)
    }

    context("resolve") {
        should("return the existing user when one is known for the issuer & subject") {
            // given
            val identity = UserIdentityGenerators.generator.next()
            val existing = UserEntityGenerators.generatorFor(identity).next()
            every { userRepositoryMock.findByIssuerAndSubject(identity.issuer, identity.subject) } returns existing

            // when
            val returnedUser = cut.resolve(identity)

            // then
            returnedUser.id shouldBe existing.id
            returnedUser.username shouldBe identity.username
            returnedUser.type shouldBe UserType.HUMAN
            verify(exactly = 0) { userRepositoryMock.save(any()) }
            verify(exactly = 0) { userRepositoryMock.saveAndFlush(any()) }
        }

        should("create a new user when the issuer & subject are seen for the first time") {
            // given
            val identity = UserIdentityGenerators.generator.next()
            every { userRepositoryMock.findByIssuerAndSubject(identity.issuer, identity.subject) } returns null
            val savedSlot = slot<UserEntity>()
            every { userRepositoryMock.saveAndFlush(capture(savedSlot)) } returns
                UserEntityGenerators.generatorFor(identity).next()

            // when
            val returnedUser = cut.resolve(identity)

            // then
            returnedUser.subject shouldBe identity.subject
            savedSlot.captured.issuer shouldBe identity.issuer
            savedSlot.captured.subject shouldBe identity.subject
            savedSlot.captured.username shouldBe identity.username
            savedSlot.captured.displayName shouldBe identity.displayName
            savedSlot.captured.type shouldBe UserTypeEntity.HUMAN
        }

        should("give a newly created user an org unit named after their display name") {
            // given
            val identity = UserIdentityGenerators.generator.next().copy(displayName = "Robin")
            val created = UserEntityGenerators.generatorFor(identity).next()
            every { userRepositoryMock.findByIssuerAndSubject(identity.issuer, identity.subject) } returns null
            every { userRepositoryMock.saveAndFlush(any()) } returns created

            val createOrgUnitSlot = slot<CreateOrgUnit>()
            every { orgUnitServiceMock.create(capture(createOrgUnitSlot)) } returns
                OrgUnitGenerators.generator.next()

            // when
            cut.resolve(identity)

            // then
            createOrgUnitSlot.captured.name shouldBe "Robin's Workshop"
            createOrgUnitSlot.captured.ownerId shouldBe created.id
        }

        should("name the org unit after the username when the token carries no display name") {
            // given
            val identity = UserIdentityGenerators.generator.next()
                .copy(username = "robin", displayName = null)
            every { userRepositoryMock.findByIssuerAndSubject(identity.issuer, identity.subject) } returns null
            every { userRepositoryMock.saveAndFlush(any()) } returns
                UserEntityGenerators.generatorFor(identity).next()

            val createOrgUnitSlot = slot<CreateOrgUnit>()
            every { orgUnitServiceMock.create(capture(createOrgUnitSlot)) } returns
                OrgUnitGenerators.generator.next()

            // when
            cut.resolve(identity)

            // then
            createOrgUnitSlot.captured.name shouldBe "robin's Workshop"
        }

        should("create no org unit for a user that already exists") {
            // given
            val identity = UserIdentityGenerators.generator.next()
            every { userRepositoryMock.findByIssuerAndSubject(identity.issuer, identity.subject) } returns
                UserEntityGenerators.generatorFor(identity).next()

            // when
            cut.resolve(identity)

            // then
            verify(exactly = 0) { orgUnitServiceMock.create(any()) }
        }

        should("throw DataIntegrityViolationException when user with issuer and subject already exists") {
            // given
            val identity = UserIdentityGenerators.generator.next()
            every { userRepositoryMock.findByIssuerAndSubject(identity.issuer, identity.subject) } returns null
            every { userRepositoryMock.saveAndFlush(any()) } throws
                DataIntegrityViolationException("uq_users_issuer_subject")

            // when & then
            shouldThrow<UserAlreadyProvisionedException> {
                cut.resolve(identity)
            }
        }

        should("update the cached data when it differs from the given token") {
            // given
            val identity = UserIdentityGenerators.generator.next()
            val stored = UserEntityGenerators.generatorFor(identity).next()
                .copy(username = "old-user", displayName = "Old Name")
            every { userRepositoryMock.findByIssuerAndSubject(identity.issuer, identity.subject) } returns stored
            every { userRepositoryMock.save(any()) } returns stored

            // when
            val returnedUser = cut.resolve(identity)

            // then
            returnedUser.username shouldBe identity.username
            returnedUser.displayName shouldBe identity.displayName
            verify(exactly = 1) { userRepositoryMock.save(stored) }
        }

        should("not save when the cached data still match the given token") {
            // given
            val identity = UserIdentityGenerators.generator.next()
            every { userRepositoryMock.findByIssuerAndSubject(identity.issuer, identity.subject) } returns
                UserEntityGenerators.generatorFor(identity).next()

            // when
            cut.resolve(identity)

            // then
            verify(exactly = 0) { userRepositoryMock.save(any()) }
        }

        should("update the stored display name when the token does not contain it") {
            // given
            val identity = UserIdentityGenerators.generator.next()
            val stored = UserEntityGenerators.generatorFor(identity).next()
            every { userRepositoryMock.findByIssuerAndSubject(identity.issuer, identity.subject) } returns stored
            every { userRepositoryMock.save(any()) } returns stored

            // when
            val returnedUser = cut.resolve(identity.copy(displayName = null))

            // then
            returnedUser.displayName shouldBe null
            verify(exactly = 1) { userRepositoryMock.save(stored) }
        }
    }
})
