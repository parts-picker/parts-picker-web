package de.partspicker.web.orgunit.persistence

import de.partspicker.web.test.annotations.ReducedSpringTestContext
import de.partspicker.web.test.generators.OrgUnitEntityGenerators
import de.partspicker.web.test.generators.UserEntityGenerators
import de.partspicker.web.user.persistence.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import org.springframework.dao.DataIntegrityViolationException

@ReducedSpringTestContext
class OrgUnitRepositoryIntTest(
    private val cut: OrgUnitRepository,
    // support repositories
    private val userRepository: UserRepository
) : ShouldSpec({

    context("uq_org_units_owner_name constraint") {
        should("reject a second org unit with the same name for the same owner") {
            // given
            val owner = userRepository.saveAndFlush(UserEntityGenerators.humanGenerator.next().copy(id = 0))
            val existing = cut.saveAndFlush(OrgUnitEntityGenerators.generatorFor(owner).next().copy(id = 0))

            // when & then
            shouldThrow<DataIntegrityViolationException> {
                cut.saveAndFlush(
                    OrgUnitEntityGenerators.generatorFor(owner).next().copy(id = 0, name = existing.name)
                )
            }
        }

        should("allow the same org unit name for a different owner") {
            // given
            val owner = userRepository.saveAndFlush(UserEntityGenerators.humanGenerator.next().copy(id = 0))
            val otherOwner = userRepository.saveAndFlush(UserEntityGenerators.humanGenerator.next().copy(id = 0))
            val existing = cut.saveAndFlush(OrgUnitEntityGenerators.generatorFor(owner).next().copy(id = 0))

            // when
            val savedOrgUnit = cut.saveAndFlush(
                OrgUnitEntityGenerators.generatorFor(otherOwner).next().copy(id = 0, name = existing.name)
            )

            // then
            savedOrgUnit.name shouldBe existing.name
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
