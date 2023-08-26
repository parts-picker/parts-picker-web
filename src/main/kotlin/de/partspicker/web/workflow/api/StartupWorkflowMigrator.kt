package de.partspicker.web.workflow.api

import de.partspicker.web.workflow.business.WorkflowMigrationService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "workflow.migration", name = ["auto-migration"])
class StartupWorkflowMigrator(
    val workflowMigrationService: WorkflowMigrationService
) {
    @EventListener
    @Throws(Exception::class)
    @Suppress("UnusedPrivateMember")
    @Order(Ordered.LOWEST_PRECEDENCE) // must be executed after WorkflowStartupReader.onApplicationEvent
    fun onApplicationEvent(event: ContextRefreshedEvent?) {
        this.workflowMigrationService.migrateAllToLatestVersion()
    }
}
