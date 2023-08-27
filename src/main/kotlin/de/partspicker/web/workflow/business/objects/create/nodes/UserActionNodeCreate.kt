package de.partspicker.web.workflow.business.objects.create.nodes

class UserActionNodeCreate(
    name: String,
    displayName: String
) : NodeCreate(name, displayName) {
    init {
        check(displayName.isNotBlank())
    }
}
