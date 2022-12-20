package de.partspicker.web.project.persistance.entities

import de.partspicker.web.project.business.objects.Group
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table

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
