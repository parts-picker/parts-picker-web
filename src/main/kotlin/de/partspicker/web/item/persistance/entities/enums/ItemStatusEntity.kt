package de.partspicker.web.item.persistance.entities.enums

import de.partspicker.web.item.business.objects.enums.ItemStatus

enum class ItemStatusEntity {
    NEEDED,
    ORDERED,
    IN_TRANSIT,
    IN_STOCK,
    RESERVED,
    USED,
    DISPOSED,
    UNKNOWN;

    companion object {
        fun from(itemStatus: ItemStatus) = when (itemStatus) {
            ItemStatus.NEEDED -> NEEDED
            ItemStatus.ORDERED -> ORDERED
            ItemStatus.IN_TRANSIT -> IN_TRANSIT
            ItemStatus.IN_STOCK -> IN_STOCK
            ItemStatus.RESERVED -> RESERVED
            ItemStatus.USED -> USED
            ItemStatus.DISPOSED -> DISPOSED
            ItemStatus.UNKNOWN -> UNKNOWN
        }
    }
}
