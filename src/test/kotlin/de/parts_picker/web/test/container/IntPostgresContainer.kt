package de.parts_picker.web.test.container

import org.testcontainers.containers.PostgreSQLContainer

class IntPostgresContainer : PostgreSQLContainer<IntPostgresContainer>(postgresImageName) {
    companion object {
        private const val postgresImageName = "postgres:13.3-alpine"

        private lateinit var instance: IntPostgresContainer

        fun start() {
            if (!Companion::instance.isInitialized) {
                instance = IntPostgresContainer().apply {
                    withUsername("int-test-user")
                    withPassword("local")
                    withDatabaseName("test")
                }
                instance.start()
            }
        }

        fun stop() {
            if(Companion::instance.isInitialized && instance.isRunning) {
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