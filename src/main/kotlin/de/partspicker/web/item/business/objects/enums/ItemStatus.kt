package de.partspicker.web.item.business.objects.enums

import de.partspicker.web.item.api.requests.ItemStatusRequest
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity

enum class ItemStatus {
    NEEDED,
    ORDERED,
    IN_TRANSIT,
    IN_STOCK,
    RESERVED,
    USED,
    DISPOSED,
    UNKNOWN;

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

        fun from(itemStatusRequest: ItemStatusRequest) = when (itemStatusRequest) {
            ItemStatusRequest.ORDERED -> ORDERED
            ItemStatusRequest.IN_STOCK -> IN_STOCK
        }
    }
}
