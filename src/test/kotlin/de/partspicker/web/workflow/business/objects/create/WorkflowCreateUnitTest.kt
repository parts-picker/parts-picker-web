package de.partspicker.web.workflow.business.objects.create

import de.partspicker.web.test.generators.workflow.NodeCreateGenerators
import de.partspicker.web.test.generators.workflow.WorkflowCreateGenerators
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeDuplicateException
import de.partspicker.web.workflow.business.exceptions.WorkflowIllegalStateException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeDuplicateException
import de.partspicker.web.workflow.business.exceptions.WorkflowRouteDuplicateException
import de.partspicker.web.workflow.business.exceptions.WorkflowSemanticException
import de.partspicker.web.workflow.business.objects.create.enums.StartTypeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.StartNodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.StopNodeCreate
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single

class WorkflowCreateUnitTest : ShouldSpec({

    context("validation") {
        should("run when everything valid") {
            WorkflowCreateGenerators.generator.single()
        }

        should("throw WorkflowIllegalStateException when name is blank") {
            val exception = shouldThrow<WorkflowIllegalStateException> {
                WorkflowCreateGenerators.generator.single().copy(name = "")
            }

            exception.message shouldBe WorkflowCreate.NAME_IS_BLANK
        }

        should("throw WorkflowIllegalStateException when version smaller than one") {
            val exception = shouldThrow<WorkflowIllegalStateException> {
                WorkflowCreateGenerators.generator.single().copy(version = 0)
            }

            exception.message shouldBe WorkflowCreate.VERSION_SMALLER_AS_ONE
        }

        should("throw WorkflowIllegalStateException when no start node present") {
            val exception = shouldThrow<WorkflowIllegalStateException> {
                WorkflowCreate(
                    name = "Workflow",
                    version = 1L,
                    nodes = listOf(NodeCreateGenerators.stopNodeCreateGenerator.single()),
                    edges = emptyList()
                )
            }

            exception.message shouldBe WorkflowCreate.AT_LEAST_ONE_START_NODE
        }

        should("throw WorkflowIllegalStateException when no stop node present") {
            val exception = shouldThrow<WorkflowIllegalStateException> {
                WorkflowCreate(
                    name = "Workflow",
                    version = 1L,
                    nodes = listOf(NodeCreateGenerators.startNodeCreateGenerator.single()),
                    edges = emptyList()
                )
            }

            exception.message shouldBe WorkflowCreate.AT_LEAST_ONE_STOP_NODE
        }

        should("throw WorkflowIllegalStateException when a start node is the target node of an edge") {
            val startNode = NodeCreateGenerators.startNodeCreateGenerator.single()
            val stopNode = NodeCreateGenerators.stopNodeCreateGenerator.single()
            val node = NodeCreateGenerators.actionNodeGenerator.single()

            val exception = shouldThrow<WorkflowIllegalStateException> {
                WorkflowCreate(
                    name = "Workflow",
                    version = 1L,
                    nodes = listOf(startNode, node, stopNode),
                    edges = listOf(
                        EdgeCreate(
                            name = "faulty_edge",
                            displayName = "Faulty",
                            sourceNode = node.name,
                            targetNode = startNode.name,
                            conditions = emptyList()
                        )
                    )
                )
            }

            exception.message shouldBe WorkflowCreate.START_NODE_IS_TARGET
        }

        should("throw WorkflowIllegalStateException when a stope node is the source node of an edge") {
            val startNode = NodeCreateGenerators.startNodeCreateGenerator.single()
            val stopNode = NodeCreateGenerators.stopNodeCreateGenerator.single()
            val node = NodeCreateGenerators.actionNodeGenerator.single()

            val exception = shouldThrow<WorkflowIllegalStateException> {
                WorkflowCreate(
                    name = "Workflow",
                    version = 1L,
                    nodes = listOf(startNode, node, stopNode),
                    edges = listOf(
                        EdgeCreate(
                            name = "faulty_edge",
                            displayName = "Faulty",
                            sourceNode = stopNode.name,
                            targetNode = node.name,
                            conditions = emptyList()
                        )
                    )
                )
            }

            exception.message shouldBe WorkflowCreate.STOP_NODE_IS_SOURCE
        }

        should("throw WorkflowNodeDuplicateException when two or more nodes have the same name") {
            val exception = shouldThrow<WorkflowNodeDuplicateException> {
                WorkflowCreate(
                    name = "Workflow",
                    version = 1L,
                    nodes = listOf(
                        StartNodeCreate("duplicated_name", "dupe", StartTypeCreate.WORKFLOW),
                        StopNodeCreate("duplicated_name", "dupe"),
                        NodeCreateGenerators.actionNodeGenerator.single()
                    ),
                    edges = emptyList()
                )
            }

            exception.message shouldBe "Two or more nodes may not have the same name: [duplicated_name]"
        }

        should("throw WorkflowEdgeDuplicateException when two or more nodes have the same name") {
            val exception = shouldThrow<WorkflowEdgeDuplicateException> {
                WorkflowCreate(
                    name = "Workflow",
                    version = 1L,
                    nodes = listOf(
                        StartNodeCreate("start_node", "start", StartTypeCreate.WORKFLOW),
                        StopNodeCreate("stop_node", "stop"),
                        NodeCreateGenerators.actionNodeGenerator.single()
                    ),
                    edges = listOf(
                        EdgeCreate(
                            name = "duplicated_name",
                            displayName = "Faulty",
                            sourceNode = "any",
                            targetNode = "any",
                            conditions = emptyList()
                        ),
                        EdgeCreate(
                            name = "duplicated_name",
                            displayName = "Faulty",
                            sourceNode = "any",
                            targetNode = "any",
                            conditions = emptyList()
                        )
                    )
                )
            }

            exception.message shouldBe "Two or more edges may not have the same name: [duplicated_name]"
        }

        should("throw WorkflowRouteDuplicateException when two or more edges have the same source & target nodes") {
            val exception = shouldThrow<WorkflowRouteDuplicateException> {
                WorkflowCreate(
                    name = "Workflow",
                    version = 1L,
                    nodes = listOf(
                        StartNodeCreate("start_node", "start", StartTypeCreate.WORKFLOW),
                        StopNodeCreate("stop_node", "stop")
                    ),
                    edges = listOf(
                        EdgeCreate(
                            name = "edge1",
                            displayName = "Faulty",
                            sourceNode = "start_node",
                            targetNode = "stop_node",
                            conditions = emptyList()
                        ),
                        EdgeCreate(
                            name = "edge2",
                            displayName = "Faulty",
                            sourceNode = "start_node",
                            targetNode = "stop_node",
                            conditions = emptyList()
                        )
                    )
                )
            }

            exception.message shouldBe "Routes described by edges must be unique. Duplicated routes: [edge1, edge2]"
        }

        should("throw WorkflowSemanticException when a node is not a stop node and has no routes") {
            val startNode = StartNodeCreate("start_node", "start", StartTypeCreate.WORKFLOW)
            val node = NodeCreateGenerators.actionNodeGenerator.single()
            val stopNode = StopNodeCreate("stop_node", "stop")

            val exception = shouldThrow<WorkflowSemanticException> {
                WorkflowCreate(
                    name = "Workflow",
                    version = 1L,
                    nodes = listOf(startNode, node, stopNode),
                    edges = listOf(
                        EdgeCreate(
                            name = "edge1",
                            displayName = "start->node",
                            sourceNode = startNode.name,
                            targetNode = node.name,
                            conditions = emptyList()
                        )
                    )
                )
            }

            exception.message shouldBe "Node with name ${node.name} is not a stop node & is not the source if any edges"
        }

        should("throw WorkflowSemanticException when a node has no connection to a start node") {
            val startNode = StartNodeCreate("start_node", "start", StartTypeCreate.WORKFLOW)
            val node = NodeCreateGenerators.actionNodeGenerator.single()
            val stopNode = StopNodeCreate("stop_node", "stop")
            val lostNode = NodeCreateGenerators.actionNodeGenerator.single()

            val exception = shouldThrow<WorkflowSemanticException> {
                WorkflowCreate(
                    name = "Workflow",
                    version = 1L,
                    nodes = listOf(startNode, node, stopNode, lostNode),
                    edges = listOf(
                        EdgeCreate(
                            name = "edge1",
                            displayName = "start->node",
                            sourceNode = startNode.name,
                            targetNode = node.name,
                            conditions = emptyList()
                        ),
                        EdgeCreate(
                            name = "edge2",
                            displayName = "node->stop",
                            sourceNode = node.name,
                            targetNode = stopNode.name,
                            conditions = emptyList()
                        )
                    )
                )
            }

            exception.message shouldBe
                "Node(s) with name(s): [${lostNode.name}] are not start nodes" +
                " & have no continuous connection from a start node to a stop node"
        }

        should("throw WorkflowSemanticException when nodes are in a loop without a connection to a stop node") {
            val startNode = StartNodeCreate("start_node", "start", StartTypeCreate.WORKFLOW)
            val node1 = NodeCreateGenerators.actionNodeGenerator.single()
            val node2 = NodeCreateGenerators.actionNodeGenerator.single()
            val stopNode = StopNodeCreate("stop_node", "stop")

            val exception = shouldThrow<WorkflowSemanticException> {
                WorkflowCreate(
                    name = "Workflow",
                    version = 1L,
                    nodes = listOf(startNode, node1, node2, stopNode),
                    edges = listOf(
                        EdgeCreate(
                            name = "edge1",
                            displayName = "start->node1",
                            sourceNode = startNode.name,
                            targetNode = node1.name,
                            conditions = emptyList()
                        ),
                        EdgeCreate(
                            name = "edge2",
                            displayName = "node1->node2",
                            sourceNode = node1.name,
                            targetNode = node2.name,
                            conditions = emptyList()
                        ),
                        EdgeCreate(
                            name = "edge3",
                            displayName = "node2->node1",
                            sourceNode = node2.name,
                            targetNode = node1.name,
                            conditions = emptyList()
                        )
                    )
                )
            }

            exception.message shouldBe
                "Node(s) with name(s): [${stopNode.name}] are not start nodes" +
                " & have no continuous connection from a start node to a stop node"
        }
    }
})
