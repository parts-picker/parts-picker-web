package de.partspicker.web.test.container

import org.testcontainers.containers.PostgreSQLContainer

class PostgresTestContainer private constructor() : PostgreSQLContainer<PostgresTestContainer>(postgresImageName) {
    companion object {
        private const val postgresImageName = "postgres:13.11-alpine"

        private lateinit var instance: PostgresTestContainer

        fun start() {
            if (!Companion::instance.isInitialized) {
                instance = PostgresTestContainer().apply {
                    withUsername("int-test-user")
                    withPassword("local")
                    withDatabaseName("test")
                }
                instance.start()
            }
        }

        fun stop() {
            if (Companion::instance.isInitialized && instance.isRunning) {
                instance.stop()
            }
        }
    }

    override fun start() {
        super.start()

        System.setProperty("spring.datasource.url", this.jdbcUrl)
        System.setProperty("spring.datasource.username", this.username)
        System.setProperty("spring.datasource.password", this.password)
    }
}
