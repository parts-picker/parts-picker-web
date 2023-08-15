package de.partspicker.web.inventory.persistence.embeddableids

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class RequiredItemTypeId(
    @Column(name = "project_id")
    val projectId: Long,
    @Column(name = "item_type_id")
    val itemTypeId: Long
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
