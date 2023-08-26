package de.partspicker.web.workflow.business.automated

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@ConditionalOnProperty(prefix = "workflow.automated-action", name = ["active"], matchIfMissing = true)
@Component
class AutomatedActionRunner(
    private val automatedActionService: AutomatedActionService
) {

    companion object : LoggingUtil {
        const val DEFAULT_WAIT_TIME = 1000L

        val logger = logger()
    }

    var applicationReady = false

    @EventListener
    @Suppress("UnusedPrivateMember")
    fun onApplicationEvent(event: ApplicationReadyEvent) {
        applicationReady = true
    }

    @Scheduled(fixedDelay = 1000L)
    fun executeAutomatedActions() {
        if (applicationReady) {
            logger.info("Starting automated action runner")
            try {
                runBlocking {
                    while (true) {
                        launch { automatedActionService.executeBatch() }
                        delay(DEFAULT_WAIT_TIME)
                    }
                }
            } catch (_: InterruptedException) {
                logger.info("Canceling automated action runner")
            }
        }
    }
}
