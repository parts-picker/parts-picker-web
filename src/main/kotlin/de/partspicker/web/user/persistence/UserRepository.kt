package de.partspicker.web.user.persistence

import de.partspicker.web.user.persistence.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {

    companion object {
        const val ISSUER_SUBJECT_CONSTRAINT = "uq_users_issuer_subject"
    }

    fun findByIssuerAndSubject(issuer: String, subject: String): UserEntity?
}
