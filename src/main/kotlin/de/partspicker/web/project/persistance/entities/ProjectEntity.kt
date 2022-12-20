package de.partspicker.web.project.persistance.entities

import de.partspicker.web.project.business.objects.Project
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Indexed
@Table(name = "projects")
data class ProjectEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_gen")
    @SequenceGenerator(name = "project_gen", sequenceName = "project_seq", allocationSize = 1)
    var id: Long = 0,

    @FullTextField
    @Column(nullable = false)
    var name: String? = null,

    @FullTextField
    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(name = "fk_group_of_project"))
    var group: GroupEntity? = null
) {
    companion object {
        fun from(project: Project) = ProjectEntity(
            id = project.id,
            name = project.name,
            description = project.description,
            group = project.group?.let { group -> GroupEntity.from(group) }
        )
    }
}
