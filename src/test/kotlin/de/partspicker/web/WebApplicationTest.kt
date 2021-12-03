package de.partspicker.web

import io.kotest.core.spec.style.ShouldSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("integration")
class WebApplicationTest : ShouldSpec({

    should("load context")
})
