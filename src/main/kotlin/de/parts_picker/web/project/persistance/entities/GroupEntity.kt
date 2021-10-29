package de.parts_picker.web.project.persistance.entities

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import javax.persistence.*

@Entity
@Indexed
@Table(name="groups",)
data class GroupEntity (
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String? = null,

    @FullTextField
    var description: String? = null
)