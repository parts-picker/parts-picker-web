package de.partspicker.web.project.persistance.entities

import de.partspicker.web.project.business.objects.CreateProject
import de.partspicker.web.workflow.persistance.entities.InstanceEntity
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
import javax.persistence.OneToOne
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
    var group: GroupEntity? = null,

    @OneToOne
    @JoinColumn(name = "instance_id", foreignKey = ForeignKey(name = "fk_instance"))
    val workflowInstance: InstanceEntity? = null
) {
    companion object {
        fun from(project: CreateProject, instanceId: Long) = ProjectEntity(
            id = project.id,
            name = project.name,
            description = project.description,
            group = project.groupId?.let { groupId -> GroupEntity(id = groupId) },
            workflowInstance = InstanceEntity(id = instanceId)
        )
    }
}
