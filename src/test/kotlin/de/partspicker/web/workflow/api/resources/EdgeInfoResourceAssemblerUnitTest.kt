package de.partspicker.web.workflow.api.resources

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.test.util.OrgUnitAccessMocks
import de.partspicker.web.test.util.linksNamed
import de.partspicker.web.workflow.business.objects.EdgeInfo
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.clearMocks
import io.mockk.mockk

class EdgeInfoResourceAssemblerUnitTest : ShouldSpec({

    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val cut = EdgeInfoResourceAssembler(orgUnitAccessServiceMock)

    val currentUserId = 7L
    val edgeInfo = EdgeInfo(id = 1L, name = "next", displayName = "Next", sourceNodeId = 2L, instanceId = 3L)

    afterTest {
        clearMocks(orgUnitAccessServiceMock)
    }

    context("toModel") {
        should("offer advancing to a member holding USE") {
            // given
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.USE, currentUserId)

            // when
            val resource = cut.toModel(edgeInfo, projectId = 4L, orgUnitId = 5L)

            // then
            resource.linksNamed(UPDATE) shouldHaveSize 1
        }

        should("offer no advancing to a member holding READ") {
            // given
            OrgUnitAccessMocks.holding(orgUnitAccessServiceMock, AccessLevel.READ, currentUserId)

            // when
            val resource = cut.toModel(edgeInfo, projectId = 4L, orgUnitId = 5L)

            // then
            resource.linksNamed(UPDATE).shouldBeEmpty()
        }
    }
})
