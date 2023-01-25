package de.partspicker.web.workflow.api.resources

import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel

class InstanceInfoResource(
    val name: String,
    val displayName: String,
    val options: Iterable<EdgeInfoResource>,
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

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + options.hashCode()
        return result
    }

    override fun toString(): String {
        return "InstanceInfoResource(name='$name', displayName='$displayName', options=$options)"
    }
}
