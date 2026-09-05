package de.partspicker.web.orgunit.business

import de.partspicker.web.common.persistence.entities.CreationInfo
import de.partspicker.web.common.persistence.entities.enums.AccessLevelEntity
import de.partspicker.web.common.util.violatedConstraint
import de.partspicker.web.orgunit.business.exceptions.OrgUnitNameAlreadyUsedException
import de.partspicker.web.orgunit.business.objects.CreateOrgUnit
import de.partspicker.web.orgunit.business.objects.OrgUnit
import de.partspicker.web.orgunit.persistence.OrgUnitEntitlementRepository
import de.partspicker.web.orgunit.persistence.OrgUnitRepository
import de.partspicker.web.orgunit.persistence.OrgUnitRepository.Companion.OWNER_NAME_CONSTRAINT
import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntitlementEntity
import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntity
import de.partspicker.web.user.business.exceptions.UserNotFoundException
import de.partspicker.web.user.persistence.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class OrgUnitService(
    private val orgUnitRepository: OrgUnitRepository,
    private val orgUnitEntitlementRepository: OrgUnitEntitlementRepository,
    private val userRepository: UserRepository
) {
    /**
     * Creates an org unit & entitles its owner to maintain it.
     */
    @Transactional
    fun create(createOrgUnit: CreateOrgUnit): OrgUnit {
        val ownerEntity = this.userRepository.findByIdOrNull(createOrgUnit.ownerId)
            ?: throw UserNotFoundException(createOrgUnit.ownerId)

        val orgUnitEntity = try {
            this.orgUnitRepository.saveAndFlush(
                OrgUnitEntity(
                    name = createOrgUnit.name,
                    shortDescription = createOrgUnit.shortDescription,
                    owner = ownerEntity,
                    creation = CreationInfo(createdBy = ownerEntity, createdOn = Instant.now())
                )
            )
        } catch (exception: DataIntegrityViolationException) {
            when (exception.violatedConstraint()) {
                OWNER_NAME_CONSTRAINT -> throw OrgUnitNameAlreadyUsedException(createOrgUnit.name, exception)
                else -> throw exception
            }
        }

        this.orgUnitEntitlementRepository.save(
            OrgUnitEntitlementEntity(
                orgUnit = orgUnitEntity,
                user = ownerEntity,
                accessLevel = AccessLevelEntity.MAINTAIN,
                joinedOn = Instant.now()
            )
        )

        return OrgUnit.from(orgUnitEntity)
    }
}
