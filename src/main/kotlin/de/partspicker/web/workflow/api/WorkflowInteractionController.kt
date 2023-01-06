package de.partspicker.web.workflow.api

import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.workflow.api.requests.AdvanceInstanceStateRequest
import de.partspicker.web.workflow.api.resources.EdgeInfoResource
import de.partspicker.web.workflow.api.resources.EdgeInfoResourceAssembler
import de.partspicker.web.workflow.api.resources.NodeInfoResource
import de.partspicker.web.workflow.api.resources.NodeInfoResourceAssembler
import de.partspicker.web.workflow.business.WorkflowInteractionService
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class WorkflowInteractionController(
    private val workflowInteractionService: WorkflowInteractionService,
    private val nodeInfoResourceAssembler: NodeInfoResourceAssembler,
    private val edgeInfoResourceAssembler: EdgeInfoResourceAssembler
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @PostMapping("/instance/{instanceId}/edges/{edgeId}")
    fun handleAdvanceInstanceState(
        @PathVariable instanceId: Long,
        @PathVariable edgeId: Long,
        @RequestBody requestBody: AdvanceInstanceStateRequest?
    ): ResponseEntity<NodeInfoResource> {
        logger.info(
            "=> POST request to advance the current node " +
                "for instance with id $instanceId through edge with id $edgeId"
        )

        val successor = this.workflowInteractionService.advanceInstanceNodeThroughEdge(
            instanceId,
            edgeId,
            requestBody?.values
        ) ?: return ResponseEntity(HttpStatus.NO_CONTENT)

        return ResponseEntity(nodeInfoResourceAssembler.toModel(successor), HttpStatus.OK)
    }

    @GetMapping("/instance/{instanceId}/node")
    fun handleGetCurrentNodeInfoForInstance(@PathVariable instanceId: Long): ResponseEntity<NodeInfoResource> {
        logger.info("=> GET request for the current node of the instance with id $instanceId")

        val nodeInfo = this.workflowInteractionService.readCurrentNodeByInstanceId(instanceId)
            ?: return ResponseEntity(HttpStatus.NO_CONTENT)

        return ResponseEntity(nodeInfoResourceAssembler.toModel(nodeInfo), HttpStatus.OK)
    }

    @GetMapping("/instance/{instanceId}/edges")
    fun handleGetAllEdgesForInstance(
        @PathVariable instanceId: Long
    ): ResponseEntity<CollectionModel<EdgeInfoResource>> {
        logger.info("=> GET request for all possible edges of the instance with id $instanceId")

        val edgeInfos = this.workflowInteractionService.readPossibleEdgesByInstanceId(instanceId)
        val edgeInfoResources = edgeInfos.map { edgeInfoResourceAssembler.toModel(it) }

        val selfLink = linkTo<WorkflowInteractionController> { handleGetAllEdgesForInstance(instanceId) }
            .withSelfRel()
            .withName(READ)
        val collectionModel = CollectionModel.of(edgeInfoResources, selfLink)

        return ResponseEntity(collectionModel, HttpStatus.OK)
    }
}
