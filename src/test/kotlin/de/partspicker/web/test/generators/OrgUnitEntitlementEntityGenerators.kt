package de.partspicker.web.test.generators

import de.partspicker.web.common.persistence.entities.enums.AccessLevelEntity
import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntitlementEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.long
import java.time.Instant

class OrgUnitEntitlementEntityGenerators private constructor() {

    companion object {
        val generator: Arb<OrgUnitEntitlementEntity> = arbitrary {
            OrgUnitEntitlementEntity(
                id = Arb.long(0).bind(),
                orgUnit = OrgUnitEntityGenerators.generator.bind(),
                user = UserEntityGenerators.humanGenerator.bind(),
                accessLevel = Arb.enum<AccessLevelEntity>().bind(),
                joinedOn = Instant.now()
            )
        }
    }
}
