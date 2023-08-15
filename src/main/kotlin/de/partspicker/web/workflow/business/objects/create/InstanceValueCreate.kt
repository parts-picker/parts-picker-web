package de.partspicker.web.workflow.business.objects.create

import de.partspicker.web.workflow.business.objects.create.enums.SupportedDataTypeCreate
import de.partspicker.web.workflow.persistence.entities.migration.InstanceValueMigrationEntity

data class InstanceValueCreate(
    val key: String,
    val value: String?,
    val type: SupportedDataTypeCreate
) {
    companion object {
        fun from(instanceValueMigrationEntity: InstanceValueMigrationEntity) = InstanceValueCreate(
            key = instanceValueMigrationEntity.key,
            value = instanceValueMigrationEntity.value,
            type = SupportedDataTypeCreate.from(instanceValueMigrationEntity.type)
        )
    }

    object AsList {
        fun from(instanceValueMigrationEntities: List<InstanceValueMigrationEntity>) =
            instanceValueMigrationEntities.map { from(it) }
    }
}
