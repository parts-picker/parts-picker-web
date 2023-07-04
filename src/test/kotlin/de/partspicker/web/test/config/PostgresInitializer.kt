package de.partspicker.web.test.config

import de.partspicker.web.test.container.PostgresTestContainer
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

class PostgresInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        PostgresTestContainer.start()
    }
}
