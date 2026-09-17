package de.partspicker.web.orgunit.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.security.CurrentUserProvider
import de.partspicker.web.common.security.SystemContext
import de.partspicker.web.orgunit.business.exceptions.CreatorOrOrgUnitAccessDeniedException
import de.partspicker.web.orgunit.business.exceptions.OrgUnitAccessDeniedException
import de.partspicker.web.test.generators.UserGenerators
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class OrgUnitAccessServiceUnitTest : ShouldSpec({

    val orgUnitEntitlementReadServiceMock = mockk<OrgUnitEntitlementReadService>()
    val currentUserProviderMock = mockk<CurrentUserProvider>()
    val systemContextMock = mockk<SystemContext>()
    val cut = OrgUnitAccessService(
        orgUnitEntitlementReadService = orgUnitEntitlementReadServiceMock,
        currentUserProvider = currentUserProviderMock,
        systemContext = systemContextMock
    )

    val orgUnitId = 42L
    val currentUser = UserGenerators.generator.next()

    beforeTest {
        every { currentUserProviderMock.getCurrentUser() } returns currentUser
    }

    afterTest {
        clearMocks(orgUnitEntitlementReadServiceMock, currentUserProviderMock, systemContextMock)
    }

    context("levelIn") {
        should("return the level the current user holds within the given org unit") {
            // given
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.USE

            // when & then
            cut.levelIn(orgUnitId) shouldBe AccessLevel.USE
        }
    }

    context("requireAtLeast") {
        should("pass when the current user holds the required level") {
            // given
            every { systemContextMock.isSystem() } returns false
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.USE

            // when & then
            shouldNotThrowAny { cut.requireAtLeast(orgUnitId, AccessLevel.USE) }
        }

        should("throw OrgUnitAccessDeniedException when the current user holds a lower level") {
            // given
            every { systemContextMock.isSystem() } returns false
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.READ

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.requireAtLeast(orgUnitId, AccessLevel.USE) }
        }

        should("throw OrgUnitAccessDeniedException when the current user may use but not configure") {
            // given
            every { systemContextMock.isSystem() } returns false
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.USE

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.requireAtLeast(orgUnitId, AccessLevel.CONFIGURE) }
        }

        should("throw OrgUnitAccessDeniedException when the current user is no member of the org unit") {
            // given
            every { systemContextMock.isSystem() } returns false
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.NONE

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.requireAtLeast(orgUnitId, AccessLevel.READ) }
        }

        should("pass & read no entitlement when running as the system") {
            // given
            every { systemContextMock.isSystem() } returns true

            // when & then
            shouldNotThrowAny { cut.requireAtLeast(orgUnitId, AccessLevel.MAINTAIN) }

            verify(exactly = 0) {
                currentUserProviderMock.getCurrentUser()
                orgUnitEntitlementReadServiceMock.accessLevelOf(any(), any())
            }
        }
    }

    context("requireMemberCreatorOrAtLeast") {
        should("pass for the creator of the object even below the required level") {
            // given
            every { systemContextMock.isSystem() } returns false
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.READ

            // when & then
            shouldNotThrowAny {
                cut.requireMemberCreatorOrAtLeast(orgUnitId, currentUser.id, AccessLevel.MAINTAIN)
            }
        }

        should("pass for another member holding the required level") {
            // given
            every { systemContextMock.isSystem() } returns false
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.MAINTAIN

            // when & then
            shouldNotThrowAny {
                cut.requireMemberCreatorOrAtLeast(orgUnitId, currentUser.id + 1, AccessLevel.MAINTAIN)
            }
        }

        should("throw CreatorOrOrgUnitAccessDeniedException for another member below the required level") {
            // given
            every { systemContextMock.isSystem() } returns false
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.USE

            // when & then
            shouldThrow<CreatorOrOrgUnitAccessDeniedException> {
                cut.requireMemberCreatorOrAtLeast(orgUnitId, currentUser.id + 1, AccessLevel.MAINTAIN)
            }
        }

        should("throw CreatorOrOrgUnitAccessDeniedException for the creator once they left the org unit") {
            // given
            every { systemContextMock.isSystem() } returns false
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.NONE

            // when & then
            shouldThrow<CreatorOrOrgUnitAccessDeniedException> {
                cut.requireMemberCreatorOrAtLeast(orgUnitId, currentUser.id, AccessLevel.MAINTAIN)
            }
        }

        should("pass & read no entitlement when running as the system") {
            // given
            every { systemContextMock.isSystem() } returns true

            // when & then
            shouldNotThrowAny {
                cut.requireMemberCreatorOrAtLeast(orgUnitId, currentUser.id + 1, AccessLevel.MAINTAIN)
            }

            verify(exactly = 0) {
                currentUserProviderMock.getCurrentUser()
                orgUnitEntitlementReadServiceMock.accessLevelOf(any(), any())
            }
        }
    }
})
