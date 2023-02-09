package de.partspicker.web.project.persistance.entities

import de.partspicker.web.project.business.objects.Group
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed

@Entity
@Indexed
@Table(name = "groups")
data class GroupEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_gen")
    @SequenceGenerator(name = "group_gen", sequenceName = "group_seq", allocationSize = 1)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String? = null,

    @FullTextField
    var description: String? = null
) {
    companion object {
        fun from(group: Group) = GroupEntity(
            id = group.id,
            name = group.name,
            description = group.description
        )
    }
}
