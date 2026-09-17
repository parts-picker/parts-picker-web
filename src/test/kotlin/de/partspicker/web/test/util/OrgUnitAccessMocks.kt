package de.partspicker.web.test.util

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.hal.DefaultName
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.orgunit.business.rules.CreatorOrOrgUnitAccessRule
import de.partspicker.web.orgunit.business.rules.OrgUnitAccessRule
import io.mockk.every
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel

object OrgUnitAccessMocks {
    /**
     * Lets the given mock hand out real rules as if the current user held [level] within every org unit.
     */
    fun holding(mock: OrgUnitAccessService, level: AccessLevel, currentUserId: Long) {
        every { mock.atLeast(any(), any()) } answers {
            OrgUnitAccessRule(grantedLevel = level, requiredLevel = secondArg(), orgUnitId = firstArg())
        }
        every { mock.memberCreatorOrAtLeast(any(), any(), any()) } answers {
            CreatorOrOrgUnitAccessRule(
                objectCreatedById = secondArg(),
                currentUserId = currentUserId,
                grantedLevel = level,
                requiredLevel = thirdArg(),
                orgUnitId = firstArg()
            )
        }
    }
}

fun RepresentationModel<*>.linksNamed(name: DefaultName): List<Link> =
    this.links.filter { it.name == name.name }
