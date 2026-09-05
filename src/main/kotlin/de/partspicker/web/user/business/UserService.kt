package de.partspicker.web.user.business

import de.partspicker.web.orgunit.business.OrgUnitService
import de.partspicker.web.orgunit.business.objects.CreateOrgUnit
import de.partspicker.web.user.business.exceptions.UserAlreadyProvisionedException
import de.partspicker.web.user.business.objects.User
import de.partspicker.web.user.business.objects.UserIdentity
import de.partspicker.web.user.persistence.UserRepository
import de.partspicker.web.user.persistence.entities.UserEntity
import de.partspicker.web.user.persistence.entities.enums.UserTypeEntity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val orgUnitService: OrgUnitService,
) {
    /**
     * Returns the user belonging to the given identity, creating it when not present in the database.
     *
     * A user created here is given an org unit of their own, so that nobody ever arrives without one.
     *
     * Throws [UserAlreadyProvisionedException] when another request creates the same user
     * before the current request is completed.
     */
    /**
     * The user with the given id as a reference, for use as a foreign key.
     */
    fun getReference(id: Long): UserEntity = this.userRepository.getReferenceById(id)

    @Transactional
    fun resolve(userIdentity: UserIdentity): User {
        val existingUserEntity = this.userRepository.findByIssuerAndSubject(userIdentity.issuer, userIdentity.subject)

        if (existingUserEntity != null) {
            return User.from(this.refreshCachedDataIfChanged(existingUserEntity, userIdentity))
        }

        val createdUser = User.from(this.create(userIdentity))
        this.orgUnitService.create(
            CreateOrgUnit(
                name = defaultOrgUnitNameFor(userIdentity),
                ownerId = createdUser.id
            )
        )

        return createdUser
    }

    private fun create(userIdentity: UserIdentity) =
        try {
            this.userRepository.saveAndFlush(
                UserEntity(
                    issuer = userIdentity.issuer,
                    subject = userIdentity.subject,
                    username = userIdentity.username,
                    displayName = userIdentity.displayName,
                    type = UserTypeEntity.HUMAN,
                ),
            )
        } catch (exception: DataIntegrityViolationException) {
            throw UserAlreadyProvisionedException(userIdentity, exception)
        }

    private fun defaultOrgUnitNameFor(userIdentity: UserIdentity) =
        "${userIdentity.displayName ?: userIdentity.username}'s Workshop"

    /**
     * Refreshes locally cached values of the given user if changes occurred.
     */
    private fun refreshCachedDataIfChanged(
        userEntity: UserEntity,
        userIdentity: UserIdentity,
    ): UserEntity {
        if (userEntity.username == userIdentity.username && userEntity.displayName == userIdentity.displayName) {
            return userEntity
        }

        userEntity.username = userIdentity.username
        userEntity.displayName = userIdentity.displayName

        return this.userRepository.save(userEntity)
    }
}
