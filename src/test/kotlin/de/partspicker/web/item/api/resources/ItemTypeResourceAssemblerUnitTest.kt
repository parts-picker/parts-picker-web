package de.partspicker.web.item.api.resources

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.DELETE
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.test.generators.ItemTypeGenerators
import de.partspicker.web.test.util.OrgUnitAccessMocks
import de.partspicker.web.test.util.linksNamed
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.mockk
import org.springframework.hateoas.IanaLinkRelations

class ItemTypeResourceAssemblerUnitTest : ShouldSpec({

    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val cut = ItemTypeResourceAssembler(orgUnitAccessServiceMock)

    val currentUserId = 7L

    afterTest {
        clearMocks(orgUnitAccessServiceMock)
    }

    context("toModel") {
        should("offer create, update & delete to a member holding MAINTAIN") {
            // given
            val itemType = ItemTypeGenerators.generator.next()
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.MAINTAIN, currentUserId)

            // when
            val resource = cut.toModel(itemType)

            // then
            resource.linksNamed(CREATE) shouldHaveSize 2
            resource.linksNamed(UPDATE) shouldHaveSize 1
            resource.linksNamed(DELETE) shouldHaveSize 1
        }

        should("offer create & update but not delete to a member holding CONFIGURE who did not create the type") {
            // given
            val itemType = ItemTypeGenerators.generator.next().copy(createdById = currentUserId + 1)
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.CONFIGURE, currentUserId)

            // when
            val resource = cut.toModel(itemType)

            // then
            resource.linksNamed(CREATE) shouldHaveSize 2
            resource.linksNamed(UPDATE) shouldHaveSize 1
            resource.linksNamed(DELETE).shouldBeEmpty()
        }

        should("offer delete to the creator of the item type below MAINTAIN") {
            // given
            val itemType = ItemTypeGenerators.generator.next().copy(createdById = currentUserId)
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.USE, currentUserId)

            // when
            val resource = cut.toModel(itemType)

            // then
            resource.linksNamed(DELETE) shouldHaveSize 1
        }

        should("offer only the item create link to a member holding USE") {
            // given
            val itemType = ItemTypeGenerators.generator.next().copy(createdById = currentUserId + 1)
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.USE, currentUserId)

            // when
            val resource = cut.toModel(itemType)

            // then
            val createLinks = resource.linksNamed(CREATE)
            createLinks shouldHaveSize 1
            createLinks.single().rel shouldBe IanaLinkRelations.DESCRIBES
            resource.linksNamed(UPDATE).shouldBeEmpty()
            resource.linksNamed(DELETE).shouldBeEmpty()
        }

        should("offer no create, update or delete to a member holding READ") {
            // given
            val itemType = ItemTypeGenerators.generator.next().copy(createdById = currentUserId + 1)
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.READ, currentUserId)

            // when
            val resource = cut.toModel(itemType)

            // then
            resource.linksNamed(CREATE).shouldBeEmpty()
            resource.linksNamed(UPDATE).shouldBeEmpty()
            resource.linksNamed(DELETE).shouldBeEmpty()
            resource.links.shouldNotBeEmpty()
        }
    }
})
