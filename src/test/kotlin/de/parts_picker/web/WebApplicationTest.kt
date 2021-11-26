package de.parts_picker.web

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("integration")
class WebApplicationTest : ShouldSpec({

    should("load context")

})
