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
     * Adds a new [Link] object to the list if the specified [condition] is true and all validation [rules] are valid.
     * If the condition is false or any validation rule fails, the link is not added to the list.
     *
     * @param link The [Link] to add to the list.
     * @param condition A Boolean condition that determines whether to add the link based on its value.
     * @param rules One or more [Rule] objects to validate the link against if the condition is true.
     *
     * @return This [LinkListBuilder] instance, with the link added if the condition and validation rules are met.
     */
    fun withCondition(link: Link, condition: Boolean, vararg rules: Rule) = if (condition) {
        this.with(link, *rules)
    } else {
        this
    }

    /**
     * Builds the list of [Link] objects that passed all validation rules.
     *
     * @return A list of valid [Link] objects.
     */
    fun build(): List<Link> = links
}
