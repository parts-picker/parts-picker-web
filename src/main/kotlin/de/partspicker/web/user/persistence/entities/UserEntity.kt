package de.partspicker.web.user.persistence.entities

import de.partspicker.web.user.persistence.entities.enums.UserTypeEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_gen")
    @SequenceGenerator(name = "user_gen", sequenceName = "user_seq", allocationSize = 1)
    var id: Long = 0,

    var issuer: String,

    var subject: String,

    var username: String,

    var displayName: String?,

    @Enumerated(EnumType.STRING)
    var type: UserTypeEntity
)
