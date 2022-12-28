package de.partspicker.web.workflow.api

import com.fasterxml.jackson.databind.ObjectMapper
import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.workflow.api.json.WorkflowJson
import de.partspicker.web.workflow.business.WorkflowService
import de.partspicker.web.workflow.business.objects.create.WorkflowCreate
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import kotlin.jvm.Throws

@Component
@ConditionalOnProperty(prefix = "workflow.input", name = ["active"])
class WorkflowStartupReader(
    @Value("classpath*:\${workflow.input.path:workflows}/*.json")
    val resources: Array<Resource>,
    @Value("\${workflow.input.path:workflows}")
    val path: String,
    val objectMapper: ObjectMapper,
    val workflowService: WorkflowService
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @EventListener
    @Throws(Exception::class)
    @Suppress("UnusedPrivateMember")
    fun onApplicationEvent(event: ContextRefreshedEvent?) {
        logCollectedResource(resources.size, path)

        resources.forEach { resource ->
            logStartProcessing(resource.filename)

            val workflow = objectMapper.readValue(resource.file, WorkflowJson::class.java)

            // check if workflow with same name and version already present in the database
            if (this.workflowService.exists(workflow.name, workflow.version)) {
                logWorkflowExists(workflow.name, workflow.version, resource.filename)
                return
            }

            // check if new version smaller than or equal to the latest version
            val latestVersion = this.workflowService.latestVersion(workflow.name) ?: 0
            if (workflow.version <= latestVersion) {
                logVersionToSmall(workflow.name, workflow.version, latestVersion, resource.filename)
                return
            }

            this.workflowService.create(WorkflowCreate.from(workflow))

            logSuccessfulCreation(workflow.name, workflow.version, resource.filename)
        }
    }

    private fun logCollectedResource(amountOfResources: Int, path: String) {
        logger.info("Collected $amountOfResources workflow json file(s) from path '$path/'")
    }

    private fun logStartProcessing(filename: String?) {
        logger.info("Processing file '$filename'")
    }

    private fun logWorkflowExists(name: String, version: Long, filename: String?) {
        logger.info("Workflow '$name' with version '$version' from file '$filename' already exists")
        logger.info("If changes were intended, please increment the version number")
    }

    private fun logVersionToSmall(name: String, version: Long, latestVersion: Long, filename: String?) {
        logger.info("Workflow '$name' from file '$filename' cannot be added with version '$version'")
        logger.info("Requested version number '$version' is smaller or equal to latest version '$latestVersion'")
        logger.info("New version should be at least '${latestVersion + 1}'")
    }

    private fun logSuccessfulCreation(name: String, version: Long, filename: String?) {
        logger.info("Workflow '$name' with version '$version' from file '$filename' added successfully")
    }
}
