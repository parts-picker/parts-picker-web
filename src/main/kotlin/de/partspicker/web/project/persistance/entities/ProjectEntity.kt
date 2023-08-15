package de.partspicker.web.project.persistance.entities

import de.partspicker.web.project.business.objects.CreateProject
import de.partspicker.web.workflow.persistence.entities.InstanceEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed

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
    var group: GroupEntity? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", foreignKey = ForeignKey(name = "fk_instance"))
    val workflowInstance: InstanceEntity? = null
) {
    companion object {
        fun from(project: CreateProject, instanceId: Long) = ProjectEntity(
            id = 0L,
            name = project.name,
            description = project.description,
            group = project.groupId?.let { groupId -> GroupEntity(id = groupId) },
            workflowInstance = InstanceEntity(id = instanceId)
        )
    }
}
