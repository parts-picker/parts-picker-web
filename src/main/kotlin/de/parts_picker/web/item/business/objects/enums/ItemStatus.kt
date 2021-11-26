package de.parts_picker.web.item.business.objects.enums

import de.parts_picker.web.item.persistance.entities.enums.ItemStatusEntity

enum class ItemStatus {
    NEEDED,
    ORDERED,
    UNSORTED,
    IN_TRANSIT,
    IN_STOCK,
    RESERVED,
    USED,
    DISPOSED,
    UNKNOWN;

    companion object {
        fun from(entity: ItemStatusEntity) = when(entity) {
            ItemStatusEntity.NEEDED -> NEEDED
            ItemStatusEntity.ORDERED -> ORDERED
            ItemStatusEntity.UNSORTED -> UNSORTED
            ItemStatusEntity.IN_TRANSIT -> IN_TRANSIT
            ItemStatusEntity.IN_STOCK -> IN_STOCK
            ItemStatusEntity.RESERVED -> RESERVED
            ItemStatusEntity.USED -> USED
            ItemStatusEntity.DISPOSED -> DISPOSED
            ItemStatusEntity.UNKNOWN -> UNKNOWN
        }
    }
}