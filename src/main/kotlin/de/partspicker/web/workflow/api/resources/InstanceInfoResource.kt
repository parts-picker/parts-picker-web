package de.partspicker.web.workflow.api.resources

import de.partspicker.web.workflow.api.resources.enums.DisplayTypeInfoResource
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel

class InstanceInfoResource(
    val name: String,
    val displayName: String,
    val options: Iterable<EdgeInfoResource>,
    val message: String?,
    val displayType: DisplayTypeInfoResource,
    links: Iterable<Link> = emptyList()
) : RepresentationModel<InstanceInfoResource>(links) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as InstanceInfoResource

        if (name != other.name) return false
        if (displayName != other.displayName) return false
        if (options != other.options) return false
        if (message != other.message) return false
        if (displayType != other.displayType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + options.hashCode()
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + displayType.hashCode()
        return result
    }

    override fun toString(): String {
        return "InstanceInfoResource(name='$name', displayName='$displayName', options=$options)"
    }
}
