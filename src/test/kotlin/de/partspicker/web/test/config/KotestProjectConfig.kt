package de.partspicker.web.test.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension

class KotestProjectConfig : AbstractProjectConfig() {
    override fun extensions(): List<Extension> = listOf(TestAuthenticationListener)
}
