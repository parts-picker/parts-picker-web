package de.partspicker.web.workflow.business.objects.create.nodes

class UserActionNodeCreate(
    name: String,
    val displayName: String
) : NodeCreate(name) {
    init {
        check(displayName.isNotBlank())
    }
}
