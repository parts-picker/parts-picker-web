package de.partspicker.web.common.persistence.entities

import de.partspicker.web.common.security.CurrentUserProvider
import de.partspicker.web.test.generators.UserEntityGenerators
import de.partspicker.web.test.generators.UserGenerators
import de.partspicker.web.user.business.UserService
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class CreationInfoFactoryUnitTest : ShouldSpec({

    val currentUserProviderMock = mockk<CurrentUserProvider>()
    val userServiceMock = mockk<UserService>()
    val now = Instant.parse("2026-09-10T12:00:00Z")
    val cut = CreationInfoFactory(
        currentUserProvider = currentUserProviderMock,
        userService = userServiceMock,
        clock = Clock.fixed(now, ZoneOffset.UTC)
    )

    afterTest {
        clearMocks(currentUserProviderMock, userServiceMock)
    }

    context("forCurrentUser") {
        should("return the creation info of the current user at the current time") {
            // given
            val currentUser = UserGenerators.generator.next()
            val currentUserEntity = UserEntityGenerators.humanGenerator.next().copy(id = currentUser.id)
            every { currentUserProviderMock.getCurrentUser() } returns currentUser
            every { userServiceMock.getReference(currentUser.id) } returns currentUserEntity

            // when
            val creationInfo = cut.forCurrentUser()

            // then
            creationInfo.createdBy shouldBe currentUserEntity
            creationInfo.createdOn shouldBe now
        }
    }
})
