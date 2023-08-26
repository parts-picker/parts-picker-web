package de.partspicker.web.workflow.business.automated

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@ConditionalOnProperty(prefix = "workflow.automated-action.", name = ["active"], matchIfMissing = true)
@Configuration
class SchedulerConfig
