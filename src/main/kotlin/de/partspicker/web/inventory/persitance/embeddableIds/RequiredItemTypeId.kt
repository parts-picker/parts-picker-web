package de.partspicker.web.inventory.persitance.embeddableIds

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class RequiredItemTypeId(
    @Column(name = "project_id")
    val projectId: Long,
    @Column(name = "item_type_id")
    val itemTypeId: Long
) : Serializable {
    companion object {
        const val serialVersionUID = 1L
    }
}
