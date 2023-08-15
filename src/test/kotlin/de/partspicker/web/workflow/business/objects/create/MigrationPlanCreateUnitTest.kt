package de.partspicker.web.workflow.business.objects.create

import de.partspicker.web.test.generators.workflow.EdgeGenerators
import de.partspicker.web.test.generators.workflow.NodeGenerators
import de.partspicker.web.workflow.api.json.EdgeJson
import de.partspicker.web.workflow.api.json.WorkflowJson
import de.partspicker.web.workflow.api.json.enums.StartTypeJson
import de.partspicker.web.workflow.api.json.migration.ExplicitMigrationPlanJson
import de.partspicker.web.workflow.api.json.migration.NodeMigrationJson
import de.partspicker.web.workflow.api.json.nodes.StartNodeJson
import de.partspicker.web.workflow.api.json.nodes.StopNodeJson
import de.partspicker.web.workflow.api.json.nodes.UserActionNodeJson
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationDuplicateSourceNodeException
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationImplicitNodeRuleCreationException
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationSourceNodeNotFoundException
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationTargetNodeNotFoundException
import de.partspicker.web.workflow.business.objects.Workflow
import de.partspicker.web.workflow.business.objects.create.migration.MigrationPlanCreate
import de.partspicker.web.workflow.business.objects.create.migration.NodeMigrationCreate
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single

class MigrationPlanCreateUnitTest : ShouldSpec({

    context("from new workflow json & latest workflow") {
        should("create implicit rules & return a migration plan when given workflow without a migration plan") {
            // given
            val startNode = NodeGenerators.startNodeGenerator.single()
            val userNode = NodeGenerators.userActionNodeGenerator.single()
            val stopNode = NodeGenerators.stopNodeGenerator.single()

            val edges = EdgeGenerators.generatorWithNodes(listOf(userNode.id), startNode.id, stopNode.id).values

            val sourceWorkflow = Workflow(
                id = 1L,
                name = "old",
                version = 1L,
                nodes = listOf(startNode, userNode, stopNode),
                edges = edges
            )

            val migrationPlan = ExplicitMigrationPlanJson(
                emptyList()
            )
            val tragetWorkflowJson = WorkflowJson(
                name = "new",
                version = 2L,
                nodes = listOf(
                    StartNodeJson(
                        name = startNode.name,
                        displayName = startNode.displayName,
                        startType = StartTypeJson.WORKFLOW
                    ),
                    UserActionNodeJson(
                        name = userNode.name,
                        displayName = userNode.displayName,
                    ),
                    StopNodeJson(
                        name = stopNode.name,
                        displayName = stopNode.displayName,
                    )
                ),
                edges = listOf(
                    EdgeJson(
                        name = "1-2",
                        displayName = "1-2",
                        sourceNode = startNode.name,
                        targetNode = userNode.name
                    ),
                    EdgeJson(
                        name = "2-3",
                        displayName = "2-3",
                        sourceNode = userNode.name,
                        targetNode = stopNode.name
                    ),
                ),
                migrationPlan = migrationPlan
            )

            // when
            val migrationPlanCreate = MigrationPlanCreate.from(tragetWorkflowJson, sourceWorkflow)

            // then
            migrationPlanCreate.nodeMigrations shouldHaveSize 3
            migrationPlanCreate.nodeMigrations shouldContainAll listOf(
                NodeMigrationCreate(
                    sourceNode = startNode.name,
                    targetNode = startNode.name,
                    instanceValueMigrations = emptyList()
                ),
                NodeMigrationCreate(
                    sourceNode = userNode.name,
                    targetNode = userNode.name,
                    instanceValueMigrations = emptyList()
                ),
                NodeMigrationCreate(
                    sourceNode = stopNode.name,
                    targetNode = stopNode.name,
                    instanceValueMigrations = emptyList()
                ),
            )
        }

        should("create implicit rules & return a migration plan when given workflow with a migration plan") {
            // given
            val startNode = NodeGenerators.startNodeGenerator.single()
            val userNode = NodeGenerators.userActionNodeGenerator.single()
            val stopNode = NodeGenerators.stopNodeGenerator.single()

            val edges = EdgeGenerators.generatorWithNodes(listOf(userNode.id), startNode.id, stopNode.id).values

            val sourceWorkflow = Workflow(
                id = 1L,
                name = "old",
                version = 1L,
                nodes = listOf(startNode, userNode, stopNode),
                edges = edges
            )

            val newStartNodeName = startNode.name + "_new_start_node_name"
            val migrationRule = NodeMigrationJson(
                sourceNode = startNode.name,
                targetNode = newStartNodeName,
                instanceValueMigrations = emptyList()
            )
            val migrationPlan = ExplicitMigrationPlanJson(
                listOf(migrationRule)
            )

            val tragetWorkflowJson = WorkflowJson(
                name = "new",
                version = 2L,
                nodes = listOf(
                    StartNodeJson(
                        name = newStartNodeName,
                        displayName = startNode.displayName,
                        startType = StartTypeJson.WORKFLOW
                    ),
                    UserActionNodeJson(
                        name = userNode.name,
                        displayName = userNode.displayName,
                    ),
                    StopNodeJson(
                        name = stopNode.name,
                        displayName = stopNode.displayName,
                    )
                ),
                edges = listOf(
                    EdgeJson(
                        name = "1-2",
                        displayName = "1-2",
                        sourceNode = startNode.name,
                        targetNode = userNode.name
                    ),
                    EdgeJson(
                        name = "2-3",
                        displayName = "2-3",
                        sourceNode = userNode.name,
                        targetNode = stopNode.name
                    ),
                ),
                migrationPlan = migrationPlan
            )

            // when
            val migrationPlanCreate = MigrationPlanCreate.from(tragetWorkflowJson, sourceWorkflow)

            // then
            migrationPlanCreate.nodeMigrations shouldHaveSize 3
            migrationPlanCreate.nodeMigrations shouldContainAll listOf(
                NodeMigrationCreate(
                    sourceNode = startNode.name,
                    targetNode = newStartNodeName,
                    instanceValueMigrations = emptyList()
                ),
                NodeMigrationCreate(
                    sourceNode = userNode.name,
                    targetNode = userNode.name,
                    instanceValueMigrations = emptyList()
                ),
                NodeMigrationCreate(
                    sourceNode = stopNode.name,
                    targetNode = stopNode.name,
                    instanceValueMigrations = emptyList()
                ),
            )
        }

        should("throw Exception when given workflow with rules with duplicated source nodes") {
            // given
            val startNode = NodeGenerators.startNodeGenerator.single()
            val userNode = NodeGenerators.userActionNodeGenerator.single()
            val stopNode = NodeGenerators.stopNodeGenerator.single()

            val edges = EdgeGenerators.generatorWithNodes(listOf(userNode.id), startNode.id, stopNode.id).values

            val sourceWorkflow = Workflow(
                id = 1L,
                name = "old",
                version = 1L,
                nodes = listOf(startNode, userNode, stopNode),
                edges = edges
            )

            val migrationPlan = ExplicitMigrationPlanJson(
                listOf(
                    NodeMigrationJson(
                        sourceNode = startNode.name,
                        targetNode = startNode.name,
                        instanceValueMigrations = emptyList()
                    ),
                    NodeMigrationJson(
                        sourceNode = startNode.name,
                        targetNode = startNode.name,
                        instanceValueMigrations = emptyList()
                    )
                )
            )

            val tragetWorkflowJson = WorkflowJson(
                name = "new",
                version = 2L,
                nodes = listOf(
                    StartNodeJson(
                        name = startNode.name,
                        displayName = startNode.displayName,
                        startType = StartTypeJson.WORKFLOW
                    ),
                    UserActionNodeJson(
                        name = userNode.name,
                        displayName = userNode.displayName,
                    ),
                    StopNodeJson(
                        name = stopNode.name,
                        displayName = stopNode.displayName,
                    )
                ),
                edges = listOf(
                    EdgeJson(
                        name = "1-2",
                        displayName = "1-2",
                        sourceNode = startNode.name,
                        targetNode = userNode.name
                    ),
                    EdgeJson(
                        name = "2-3",
                        displayName = "2-3",
                        sourceNode = userNode.name,
                        targetNode = stopNode.name
                    ),
                ),
                migrationPlan = migrationPlan
            )

            // when
            val exception = shouldThrow<WorkflowMigrationDuplicateSourceNodeException> {
                MigrationPlanCreate.from(tragetWorkflowJson, sourceWorkflow)
            }

            // then
            val expectedMessage = "Two or more node migrations may not have the same " +
                "source node name: [${startNode.name}]"
            exception.message shouldBe expectedMessage
        }

        should("throw Exception when given workflow with deleted node & no explicit migration rule") {
            // given
            val startNode = NodeGenerators.startNodeGenerator.single()
            val userNode = NodeGenerators.userActionNodeGenerator.single()
            val stopNode = NodeGenerators.stopNodeGenerator.single()

            val edges = EdgeGenerators.generatorWithNodes(listOf(userNode.id), startNode.id, stopNode.id).values

            val sourceWorkflow = Workflow(
                id = 1L,
                name = "old",
                version = 1L,
                nodes = listOf(startNode, userNode, stopNode),
                edges = edges
            )

            val migrationPlan = ExplicitMigrationPlanJson(
                emptyList()
            )
            val tragetWorkflowJson = WorkflowJson(
                name = "new",
                version = 2L,
                nodes = listOf(
                    StartNodeJson(
                        name = startNode.name,
                        displayName = startNode.displayName,
                        startType = StartTypeJson.WORKFLOW
                    ),
                    StopNodeJson(
                        name = stopNode.name,
                        displayName = stopNode.displayName,
                    )
                ),
                edges = listOf(
                    EdgeJson(
                        name = "1-2",
                        displayName = "1-2",
                        sourceNode = startNode.name,
                        targetNode = stopNode.name
                    ),
                ),
                migrationPlan = migrationPlan
            )

            // when
            val exception = shouldThrow<WorkflowMigrationImplicitNodeRuleCreationException> {
                MigrationPlanCreate.from(tragetWorkflowJson, sourceWorkflow)
            }

            // then
            val expectedMessage = "Implicit migration rule for the node with name '${userNode.name}' cannot " +
                "be created, explicit migration rule is needed"
            exception.message shouldBe expectedMessage
        }

        should("throw Exception when given migration rule for non-existing source node") {
            // given
            val startNode = NodeGenerators.startNodeGenerator.single()
            val userNode = NodeGenerators.userActionNodeGenerator.single()
            val stopNode = NodeGenerators.stopNodeGenerator.single()

            val edges = EdgeGenerators.generatorWithNodes(listOf(userNode.id), startNode.id, stopNode.id).values

            val sourceWorkflow = Workflow(
                id = 1L,
                name = "old",
                version = 1L,
                nodes = listOf(startNode, userNode, stopNode),
                edges = edges
            )

            val nonExistentSourceNodeName = "non-existent node"
            val migrationRule = NodeMigrationJson(
                sourceNode = nonExistentSourceNodeName,
                targetNode = startNode.name,
                instanceValueMigrations = emptyList()
            )
            val migrationPlan = ExplicitMigrationPlanJson(
                listOf(migrationRule)
            )
            val tragetWorkflowJson = WorkflowJson(
                name = "new",
                version = 2L,
                nodes = listOf(
                    StartNodeJson(
                        name = startNode.name,
                        displayName = startNode.displayName,
                        startType = StartTypeJson.WORKFLOW
                    ),
                    UserActionNodeJson(
                        name = userNode.name,
                        displayName = userNode.displayName,
                    ),
                    StopNodeJson(
                        name = stopNode.name,
                        displayName = stopNode.displayName,
                    )
                ),
                edges = listOf(
                    EdgeJson(
                        name = "1-2",
                        displayName = "1-2",
                        sourceNode = startNode.name,
                        targetNode = userNode.name
                    ),
                    EdgeJson(
                        name = "2-3",
                        displayName = "2-3",
                        sourceNode = userNode.name,
                        targetNode = stopNode.name
                    ),
                ),
                migrationPlan = migrationPlan
            )

            // when
            val exception = shouldThrow<WorkflowMigrationSourceNodeNotFoundException> {
                MigrationPlanCreate.from(tragetWorkflowJson, sourceWorkflow)
            }

            // then
            val expectedMessage = "The source node(s) with the name(s) '[$nonExistentSourceNodeName]' could not" +
                " be found for the given migration plan."
            exception.message shouldBe expectedMessage
        }

        should("throw Exception when given migration rule for non-existing target node") {
            // given
            val startNode = NodeGenerators.startNodeGenerator.single()
            val userNode = NodeGenerators.userActionNodeGenerator.single()
            val stopNode = NodeGenerators.stopNodeGenerator.single()

            val edges = EdgeGenerators.generatorWithNodes(listOf(userNode.id), startNode.id, stopNode.id).values

            val sourceWorkflow = Workflow(
                id = 1L,
                name = "old",
                version = 1L,
                nodes = listOf(startNode, userNode, stopNode),
                edges = edges
            )

            val missingTargetNodeName = "non-existent node"
            val migrationRule = NodeMigrationJson(
                sourceNode = startNode.name,
                targetNode = missingTargetNodeName,
                instanceValueMigrations = emptyList()
            )
            val migrationPlan = ExplicitMigrationPlanJson(
                listOf(migrationRule)
            )
            val tragetWorkflowJson = WorkflowJson(
                name = "new",
                version = 2L,
                nodes = listOf(
                    StartNodeJson(
                        name = startNode.name,
                        displayName = startNode.displayName,
                        startType = StartTypeJson.WORKFLOW
                    ),
                    UserActionNodeJson(
                        name = userNode.name,
                        displayName = userNode.displayName,
                    ),
                    StopNodeJson(
                        name = stopNode.name,
                        displayName = stopNode.displayName,
                    )
                ),
                edges = listOf(
                    EdgeJson(
                        name = "1-2",
                        displayName = "1-2",
                        sourceNode = startNode.name,
                        targetNode = userNode.name
                    ),
                    EdgeJson(
                        name = "2-3",
                        displayName = "2-3",
                        sourceNode = userNode.name,
                        targetNode = stopNode.name
                    ),
                ),
                migrationPlan = migrationPlan
            )

            // when
            val exception = shouldThrow<WorkflowMigrationTargetNodeNotFoundException> {
                MigrationPlanCreate.from(tragetWorkflowJson, sourceWorkflow)
            }

            // then
            val expectedMessage = "The target node(s) with the name(s) '[$missingTargetNodeName]' could not " +
                "be found for the given migration plan."
            exception.message shouldBe expectedMessage
        }
    }
})
