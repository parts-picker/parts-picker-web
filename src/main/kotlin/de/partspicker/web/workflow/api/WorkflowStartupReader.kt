package de.partspicker.web.workflow.api

import com.fasterxml.jackson.databind.ObjectMapper
import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.workflow.api.exceptions.WorkflowStartupReadException
import de.partspicker.web.workflow.api.json.WorkflowJson
import de.partspicker.web.workflow.business.WorkflowService
import de.partspicker.web.workflow.business.exceptions.WorkflowAlreadyExistsException
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeDuplicateException
import de.partspicker.web.workflow.business.exceptions.WorkflowIllegalStateException
import de.partspicker.web.workflow.business.exceptions.WorkflowLatestVersionIsGreaterException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeDuplicateException
import de.partspicker.web.workflow.business.exceptions.WorkflowRouteDuplicateException
import de.partspicker.web.workflow.business.exceptions.WorkflowSemanticException
import de.partspicker.web.workflow.business.objects.create.WorkflowCreate
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
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
    @Suppress("UnusedPrivateMember", "TooGenericExceptionCaught")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun onApplicationEvent(event: ContextRefreshedEvent?) {
        logCollectedResource(resources.size, path)

        resources.forEach { resource ->
            logStartProcessing(resource.filename)

            val newWorkflow = objectMapper.readValue(resource.inputStream, WorkflowJson::class.java)
            val latestWorkflow = this.workflowService.readLatest(newWorkflow.name)

            try {
                this.workflowService.create(WorkflowCreate.from(newWorkflow, latestWorkflow))
            } catch (ex: Exception) {
                when (ex) {
                    is WorkflowLatestVersionIsGreaterException -> {
                        logVersionToSmall(
                            name = ex.name,
                            version = ex.requestedVersion,
                            latestVersion = ex.requestedVersion,
                            filename = resource.filename
                        )
                        return
                    }

                    is WorkflowAlreadyExistsException -> {
                        logWorkflowExists(ex.name, ex.version, resource.filename)
                        return
                    }

                    is WorkflowIllegalStateException,
                    is WorkflowNodeDuplicateException,
                    is WorkflowEdgeDuplicateException,
                    is WorkflowRouteDuplicateException,
                    is WorkflowSemanticException,
                    is IllegalArgumentException
                    -> throw WorkflowStartupReadException.from(
                        name = newWorkflow.name,
                        version = newWorkflow.version,
                        sourcePath = resource.uri.path,
                        cause = ex
                    )

                    else -> throw ex
                }
            }

            logSuccessfulCreation(newWorkflow.name, newWorkflow.version, resource.filename)
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
