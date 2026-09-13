package de.partspicker.web.test.generators

import de.partspicker.web.orgunit.business.objects.OrgUnit
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map

object OrgUnitGenerators {
    val generator: Arb<OrgUnit> = OrgUnitEntityGenerators.generator.map { OrgUnit.from(it) }
}
