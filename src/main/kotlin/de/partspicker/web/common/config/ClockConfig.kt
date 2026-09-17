package de.partspicker.web.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class ClockConfig {

    @Bean("utcClock")
    fun clock(): Clock = Clock.systemUTC()
}
