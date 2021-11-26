package de.parts_picker.web.project.persistance.entities

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import javax.persistence.*

@Entity
@Indexed
@Table(name="projects")
data class ProjectEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_gen")
    @SequenceGenerator(name = "project_gen", sequenceName = "project_seq", allocationSize = 1)
    var id: Long? = null,

    @FullTextField
    @Column(nullable = false)
    var name: String,

    @FullTextField
    var description: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(name = "fk_group_of_project"))
    var group: GroupEntity?,
)

