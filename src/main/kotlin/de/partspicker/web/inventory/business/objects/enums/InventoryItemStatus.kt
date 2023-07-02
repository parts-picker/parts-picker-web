package de.partspicker.web.inventory.business.objects.enums

import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity

enum class InventoryItemStatus {
    NEEDED,
    ORDERED,
    IN_TRANSIT,
    IN_STOCK,
    RESERVED,
    USED,
    DISPOSED,
    UNKNOWN,
    ;

    companion object {
        fun from(entity: ItemStatusEntity) = when (entity) {
            ItemStatusEntity.NEEDED -> NEEDED
            ItemStatusEntity.ORDERED -> ORDERED
            ItemStatusEntity.IN_TRANSIT -> IN_TRANSIT
            ItemStatusEntity.IN_STOCK -> IN_STOCK
            ItemStatusEntity.RESERVED -> RESERVED
            ItemStatusEntity.USED -> USED
            ItemStatusEntity.DISPOSED -> DISPOSED
            ItemStatusEntity.UNKNOWN -> UNKNOWN
        }
    }
}
