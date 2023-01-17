package de.partspicker.web.workflow.business.exceptions

class WorkflowNodeHasMoreThanOneTargetException(offendingNodes: Map<String, Iterable<String>>) : RuntimeException(
    generateMessage(offendingNodes)
) {
    companion object {
        fun generateMessage(offendingNodes: Map<String, Iterable<String>>): String {
            val nodeMessage = offendingNodes
                .map { "Node with name '${it.key}' with edges with names ${it.value}" }
                .joinToString("\n")
            return "One or more nodes who may have only one target have multiple targets:\n $nodeMessage"
        }
    }
}
