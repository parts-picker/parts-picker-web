package de.partspicker.web.common.persistence.entities

import de.partspicker.web.user.persistence.entities.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.Instant

/**
 * Meta information about the creation of an entity.
 */
@Embeddable
data class CreationInfo(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    val createdBy: UserEntity,

    @Column(name = "created_on", nullable = false)
    val createdOn: Instant
)
