package de.partspicker.web.inventory.api.resources

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.inventory.business.objects.AvailableItemType
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.test.util.OrgUnitAccessMocks
import de.partspicker.web.test.util.linksNamed
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.mockk.clearMocks
import io.mockk.mockk

class AvailableItemTypeResourceAssemblerUnitTest : ShouldSpec({

    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val cut = AvailableItemTypeResourceAssembler(orgUnitAccessServiceMock)

    val currentUserId = 7L

    afterTest {
        clearMocks(orgUnitAccessServiceMock)
    }

    context("toModel") {
        should("offer requiring the item type to a member holding USE while the project is planned") {
            // given
            val availableItemType = AvailableItemType(
                id = 1L,
                name = "an item type",
                projectId = 2L,
                projectStatus = "planning",
                orgUnitId = 3L
            )
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.USE, currentUserId)

            // when
            val resource = cut.toModel(availableItemType)

            // then
            resource.linksNamed(CREATE) shouldHaveSize 1
        }

        should("offer no requiring to a member holding READ") {
            // given
            val availableItemType = AvailableItemType(
                id = 1L,
                name = "an item type",
                projectId = 2L,
                projectStatus = "planning",
                orgUnitId = 3L
            )
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.READ, currentUserId)

            // when
            val resource = cut.toModel(availableItemType)

            // then
            resource.linksNamed(CREATE).shouldBeEmpty()
            resource.links.shouldNotBeEmpty()
        }
    }
})
