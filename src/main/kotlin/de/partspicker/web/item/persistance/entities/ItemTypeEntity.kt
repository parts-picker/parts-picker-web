package de.partspicker.web.item.persistance.entities

import de.partspicker.web.common.persistence.entities.CreationInfo
import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntity
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.search.engine.backend.types.Projectable.YES
import org.hibernate.search.engine.backend.types.Searchable.NO
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency

@Entity
@Indexed
@Table(name = "item_types")
data class ItemTypeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_type_gen")
    @SequenceGenerator(name = "item_type_gen", sequenceName = "item_type_seq", allocationSize = 1)
    @GenericField(projectable = YES, searchable = NO)
    val id: Long = 0,

    @FullTextField(projectable = YES)
    var name: String? = null,

    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_unit_id", foreignKey = ForeignKey(name = "fk_org_unit_of_item_type"))
    @IndexedEmbedded(includePaths = [ORG_UNIT_ID_PATH])
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    var orgUnit: OrgUnitEntity,

    @Embedded
    var creation: CreationInfo
) {
    companion object {
        const val ORG_UNIT_ID_PATH = "id"
        const val ORG_UNIT_ID_FIELD_NAME = "orgUnit.$ORG_UNIT_ID_PATH"
    }
}
