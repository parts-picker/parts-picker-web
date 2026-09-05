package de.partspicker.web.orgunit.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.security.CurrentUserProvider
import de.partspicker.web.orgunit.business.exceptions.CreatorOrOrgUnitAccessDeniedException
import de.partspicker.web.orgunit.business.exceptions.OrgUnitAccessDeniedException
import de.partspicker.web.test.generators.UserEntityGenerators
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk

class OrgUnitAccessServiceUnitTest : ShouldSpec({

    val orgUnitEntitlementReadServiceMock = mockk<OrgUnitEntitlementReadService>()
    val currentUserProviderMock = mockk<CurrentUserProvider>()
    val cut = OrgUnitAccessService(
        orgUnitEntitlementReadService = orgUnitEntitlementReadServiceMock,
        currentUserProvider = currentUserProviderMock
    )

    val orgUnitId = 42L
    val currentUser = UserEntityGenerators.humanGenerator.next()

    beforeTest {
        every { currentUserProviderMock.getCurrentUserEntity() } returns currentUser
    }

    afterTest {
        clearMocks(orgUnitEntitlementReadServiceMock, currentUserProviderMock)
    }

    context("levelIn") {
        should("return the level the current user holds within the given org unit") {
            // given
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.EDIT

            // when & then
            cut.levelIn(orgUnitId) shouldBe AccessLevel.EDIT
        }
    }

    context("requireAtLeast") {
        should("pass when the current user holds the required level") {
            // given
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.EDIT

            // when & then
            shouldNotThrowAny { cut.requireAtLeast(orgUnitId, AccessLevel.EDIT) }
        }

        should("throw OrgUnitAccessDeniedException when the current user holds a lower level") {
            // given
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.READ

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.requireAtLeast(orgUnitId, AccessLevel.EDIT) }
        }

        should("throw OrgUnitAccessDeniedException when the current user is no member of the org unit") {
            // given
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.NONE

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.requireAtLeast(orgUnitId, AccessLevel.READ) }
        }
    }

    context("requireMemberCreatorOrAtLeast") {
        should("pass for the creator of the object even below the required level") {
            // given
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
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.EDIT

            // when & then
            shouldThrow<CreatorOrOrgUnitAccessDeniedException> {
                cut.requireMemberCreatorOrAtLeast(orgUnitId, currentUser.id + 1, AccessLevel.MAINTAIN)
            }
        }

        should("throw CreatorOrOrgUnitAccessDeniedException for the creator once they left the org unit") {
            // given
            every {
                orgUnitEntitlementReadServiceMock.accessLevelOf(orgUnitId, currentUser.id)
            } returns AccessLevel.NONE

            // when & then
            shouldThrow<CreatorOrOrgUnitAccessDeniedException> {
                cut.requireMemberCreatorOrAtLeast(orgUnitId, currentUser.id, AccessLevel.MAINTAIN)
            }
        }
    }
})
