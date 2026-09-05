package de.partspicker.web.orgunit.persistence

import de.partspicker.web.common.persistence.entities.enums.AccessLevelEntity
import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntitlementEntity
import de.partspicker.web.test.annotations.ReducedSpringTestContext
import de.partspicker.web.test.generators.OrgUnitEntityGenerators
import de.partspicker.web.test.generators.UserEntityGenerators
import de.partspicker.web.user.persistence.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import org.springframework.dao.DataIntegrityViolationException
import java.time.Instant

@ReducedSpringTestContext
class OrgUnitEntitlementRepositoryIntTest(
    private val cut: OrgUnitEntitlementRepository,
    // support repositories
    private val orgUnitRepository: OrgUnitRepository,
    private val userRepository: UserRepository
) : ShouldSpec({

    context("findByOrgUnitIdAndUserId") {
        should("return the entitlement of the given user within the given org unit") {
            // given
            val owner = userRepository.saveAndFlush(UserEntityGenerators.humanGenerator.next().copy(id = 0))
            val orgUnit = orgUnitRepository.saveAndFlush(
                OrgUnitEntityGenerators.generatorFor(owner).next().copy(id = 0)
            )
            val saved = cut.saveAndFlush(
                OrgUnitEntitlementEntity(
                    orgUnit = orgUnit,
                    user = owner,
                    accessLevel = AccessLevelEntity.MAINTAIN,
                    joinedOn = Instant.now()
                )
            )

            // when
            val found = cut.findByOrgUnitIdAndUserId(orgUnit.id, owner.id)

            // then
            found shouldBe saved
            found?.accessLevel shouldBe AccessLevelEntity.MAINTAIN
            found?.joinedOn shouldBe saved.joinedOn
        }

        should("return null when the given user is no member of the given org unit") {
            // given
            val owner = userRepository.saveAndFlush(UserEntityGenerators.humanGenerator.next().copy(id = 0))
            val stranger = userRepository.saveAndFlush(UserEntityGenerators.humanGenerator.next().copy(id = 0))
            val orgUnit = orgUnitRepository.saveAndFlush(
                OrgUnitEntityGenerators.generatorFor(owner).next().copy(id = 0)
            )

            // when & then
            cut.findByOrgUnitIdAndUserId(orgUnit.id, stranger.id).shouldBeNull()
        }
    }

    context("unique constraint on org unit & user") {
        should("reject a second entitlement for the same user within the same org unit") {
            // given
            val owner = userRepository.saveAndFlush(UserEntityGenerators.humanGenerator.next().copy(id = 0))
            val orgUnit = orgUnitRepository.saveAndFlush(
                OrgUnitEntityGenerators.generatorFor(owner).next().copy(id = 0)
            )
            cut.saveAndFlush(
                OrgUnitEntitlementEntity(
                    orgUnit = orgUnit,
                    user = owner,
                    accessLevel = AccessLevelEntity.EDIT,
                    joinedOn = Instant.now()
                )
            )

            // when & then
            shouldThrow<DataIntegrityViolationException> {
                cut.saveAndFlush(
                    OrgUnitEntitlementEntity(
                        orgUnit = orgUnit,
                        user = owner,
                        accessLevel = AccessLevelEntity.READ,
                        joinedOn = Instant.now()
                    )
                )
            }
        }
    }

    context("unique constraint on owner & name") {
        should("reject a second org unit with the same name for the same owner") {
            // given
            val owner = userRepository.saveAndFlush(UserEntityGenerators.humanGenerator.next().copy(id = 0))
            val existing = orgUnitRepository.saveAndFlush(
                OrgUnitEntityGenerators.generatorFor(owner).next().copy(id = 0)
            )

            // when & then
            shouldThrow<DataIntegrityViolationException> {
                orgUnitRepository.saveAndFlush(
                    OrgUnitEntityGenerators.generatorFor(owner).next().copy(id = 0, name = existing.name)
                )
            }
        }

        should("allow the same org unit name for a different owner") {
            // given
            val owner = userRepository.saveAndFlush(UserEntityGenerators.humanGenerator.next().copy(id = 0))
            val otherOwner = userRepository.saveAndFlush(UserEntityGenerators.humanGenerator.next().copy(id = 0))
            val existing = orgUnitRepository.saveAndFlush(
                OrgUnitEntityGenerators.generatorFor(owner).next().copy(id = 0)
            )

            // when
            val saved = orgUnitRepository.saveAndFlush(
                OrgUnitEntityGenerators.generatorFor(otherOwner).next().copy(id = 0, name = existing.name)
            )

            // then
            saved.name shouldBe existing.name
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
