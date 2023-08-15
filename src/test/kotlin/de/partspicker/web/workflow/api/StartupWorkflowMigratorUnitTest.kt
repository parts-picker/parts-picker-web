package de.partspicker.web.workflow.api

import de.partspicker.web.workflow.business.WorkflowMigrationService
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class StartupWorkflowMigratorUnitTest : ShouldSpec({
    val workflowMigrationServiceMock = mockk<WorkflowMigrationService>()
    val cut = StartupWorkflowMigrator(
        workflowMigrationService = workflowMigrationServiceMock
    )

    context("onApplicationEvent") {
        should("call workflowMigrationService to start migration for all workflows") {
            // given
            every { workflowMigrationServiceMock.migrateAllToLatestVersion() } returns emptyMap()

            // when
            cut.onApplicationEvent(null)

            // then
            verify { workflowMigrationServiceMock.migrateAllToLatestVersion() }
        }
    }
})
