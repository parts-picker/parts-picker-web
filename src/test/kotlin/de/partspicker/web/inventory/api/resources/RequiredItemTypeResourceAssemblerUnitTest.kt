package de.partspicker.web.inventory.api.resources

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.DELETE
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.test.generators.inventory.RequiredItemTypeGenerators
import de.partspicker.web.test.util.OrgUnitAccessMocks
import de.partspicker.web.test.util.linksNamed
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.mockk

class RequiredItemTypeResourceAssemblerUnitTest : ShouldSpec({

    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val cut = RequiredItemTypeResourceAssembler(orgUnitAccessServiceMock)

    val currentUserId = 7L

    afterTest {
        clearMocks(orgUnitAccessServiceMock)
    }

    context("toModel") {
        should("offer create, update & delete to a member holding USE while the project is planned") {
            // given
            val requiredItemType = RequiredItemTypeGenerators.generator.next().copy(projectStatus = "planning")
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.USE, currentUserId)

            // when
            val resource = cut.toModel(requiredItemType)

            // then
            resource.linksNamed(CREATE) shouldHaveSize 1
            resource.linksNamed(UPDATE) shouldHaveSize 1
            resource.linksNamed(DELETE) shouldHaveSize 1
        }

        should("offer no create, update or delete to a member holding READ") {
            // given
            val requiredItemType = RequiredItemTypeGenerators.generator.next().copy(projectStatus = "planning")
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.READ, currentUserId)

            // when
            val resource = cut.toModel(requiredItemType)

            // then
            resource.linksNamed(CREATE).shouldBeEmpty()
            resource.linksNamed(UPDATE).shouldBeEmpty()
            resource.linksNamed(DELETE).shouldBeEmpty()
            resource.links.shouldNotBeEmpty()
        }
    }
})
