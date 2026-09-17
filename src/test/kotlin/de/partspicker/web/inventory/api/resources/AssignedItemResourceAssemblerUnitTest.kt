package de.partspicker.web.inventory.api.resources

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.test.generators.inventory.AssignedItemGenerators
import de.partspicker.web.test.util.OrgUnitAccessMocks
import de.partspicker.web.test.util.linksNamed
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.mockk

class AssignedItemResourceAssemblerUnitTest : ShouldSpec({

    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val cut = AssignedItemResourceAssembler(orgUnitAccessServiceMock)

    val currentUserId = 7L

    afterTest {
        clearMocks(orgUnitAccessServiceMock)
    }

    context("toModel") {
        should("offer unassigning to a member holding USE while the project is planned") {
            // given
            val assignedItem = AssignedItemGenerators.generator.next().copy(projectStatus = "planning")
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.USE, currentUserId)

            // when
            val resource = cut.toModel(assignedItem)

            // then
            resource.linksNamed(UPDATE) shouldHaveSize 1
        }

        should("offer no unassigning to a member holding READ") {
            // given
            val assignedItem = AssignedItemGenerators.generator.next().copy(projectStatus = "planning")
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.READ, currentUserId)

            // when
            val resource = cut.toModel(assignedItem)

            // then
            resource.linksNamed(UPDATE).shouldBeEmpty()
            resource.links.shouldNotBeEmpty()
        }
    }
})
