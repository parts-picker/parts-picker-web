package de.partspicker.web.test.annotations

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

/**
 * Annotation for integration testing with a reduced context.
 * Per default, only the repositories are available.
 * A postgres test container is started by [de.partspicker.web.test.config.PostgresInitializer].
 * Use [org.springframework.context.annotation.Import] to include other components explicitly.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
annotation class ReducedSpringTestContext
