package de.parts_picker.web.test.config

import de.parts_picker.web.test.container.IntPostgresContainer
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension


class ProjectConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(SpringExtension)

    override fun beforeAll() {
        super.beforeAll()
        IntPostgresContainer.start()
    }

    override fun afterAll() {
        super.afterAll()
        IntPostgresContainer.stop()
    }
}