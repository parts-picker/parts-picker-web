package de.partspicker.web.inventory.api.resources

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.test.generators.inventory.AssignableItemGenerators
import de.partspicker.web.test.util.OrgUnitAccessMocks
import de.partspicker.web.test.util.linksNamed
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.mockk

class AssignableItemResourceAssemblerUnitTest : ShouldSpec({

    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val cut = AssignableItemResourceAssembler(orgUnitAccessServiceMock)

    val currentUserId = 7L

    afterTest {
        clearMocks(orgUnitAccessServiceMock)
    }

    context("toModel") {
        should("offer assigning to a member holding USE while the project is planned & items are still needed") {
            // given
            val assignableItem = AssignableItemGenerators.generator.next()
                .copy(assignableToProjectStatus = "planning", requiredAmount = 5, assignedAmount = 1)
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.USE, currentUserId)

            // when
            val resource = cut.toModel(assignableItem)

            // then
            resource.linksNamed(UPDATE) shouldHaveSize 1
        }

        should("offer no assigning to a member holding READ") {
            // given
            val assignableItem = AssignableItemGenerators.generator.next()
                .copy(assignableToProjectStatus = "planning", requiredAmount = 5, assignedAmount = 1)
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.READ, currentUserId)

            // when
            val resource = cut.toModel(assignableItem)

            // then
            resource.linksNamed(UPDATE).shouldBeEmpty()
            resource.links.shouldNotBeEmpty()
        }
    }
})
