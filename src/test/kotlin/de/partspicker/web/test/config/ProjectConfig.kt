package de.partspicker.web.test.config

import de.partspicker.web.test.container.IntPostgresContainer
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

class ProjectConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(SpringExtension)

    override suspend fun beforeProject() {
        super.beforeProject()
        IntPostgresContainer.start()
    }

    override suspend fun afterProject() {
        super.afterProject()
        IntPostgresContainer.stop()
    }
}
