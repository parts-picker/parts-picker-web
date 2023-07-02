package de.partspicker.web.common.hal

import de.partspicker.web.common.business.exceptions.RuleException
import de.partspicker.web.common.business.rules.Rule
import org.springframework.hateoas.Link

/**
 * A builder for creating a list of [Link] objects, with the ability to add validation rules.
 *
 * This builder allows you to add [Link] objects to a list, while specifying one or more validation
 * rules for a given link. Only links that pass all validation rules will be included in
 * the final built list.
 */
class LinkListBuilder {
    private val links = mutableListOf<Link>()

    /**
     * Adds a new [Link] object to the list, only if all validation [rules] are valid.
     *
     * @param link The [Link] to add to the list.
     * @param rules One or more [Rule] objects to validate the link against.
     *
     * @return This [LinkListBuilder] instance.
     */
    fun with(link: Link, vararg rules: Rule): LinkListBuilder {
        try {
            rules.forEach { it.valid() }
            links.add(link)
        } catch (_: RuleException) {
            // if rules are not valid, do nothing
        }

        return this
    }

    /**
     * Builds the list of [Link] objects that passed all validation rules.
     *
     * @return A list of valid [Link] objects.
     */
    fun build(): List<Link> = links
}
