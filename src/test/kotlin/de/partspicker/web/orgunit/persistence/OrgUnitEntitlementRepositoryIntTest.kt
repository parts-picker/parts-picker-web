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
            val savedEntitlement = cut.saveAndFlush(
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
            found shouldBe savedEntitlement
            found?.accessLevel shouldBe AccessLevelEntity.MAINTAIN
            found?.joinedOn shouldBe savedEntitlement.joinedOn
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

    context("uq_entitlements_org_unit_user constraint") {
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
                    accessLevel = AccessLevelEntity.CONFIGURE,
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
}) {
    override fun extensions() = listOf(SpringExtension)
}
