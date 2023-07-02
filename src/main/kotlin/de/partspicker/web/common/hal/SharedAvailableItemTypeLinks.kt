package de.partspicker.web.common.hal

import de.partspicker.web.common.hal.DefaultName.SEARCH
import de.partspicker.web.common.hal.ParamNames.Companion.NAME_SEARCH_PARAM_NAME
import de.partspicker.web.inventory.api.AvailableItemTypeController
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.TemplateVariable
import org.springframework.hateoas.TemplateVariables
import org.springframework.hateoas.UriTemplate
import org.springframework.hateoas.server.mvc.linkTo

fun generateSearchItemsByNameLink(relation: RelationName, projectId: Long) = generateSearchItemsByNameLink(
    relation = relation.displayName,
    projectId = projectId
)

fun generateSearchItemsByNameLink(relation: LinkRelation, projectId: Long) = generateSearchItemsByNameLink(
    relation = relation.toString(),
    projectId = projectId
)

fun generateSearchItemsByNameLink(relation: String, projectId: Long): Link {
    // READ availableItemTypes link with search meta information params
    val linkWithoutNameParam = linkTo<AvailableItemTypeController> { handleSearch(projectId, "") }
        .toUriComponentsBuilder()
        .replaceQueryParam("name", null)
        .toUriString()

    val uriTemplate = UriTemplate.of(linkWithoutNameParam)
        .with(TemplateVariables(TemplateVariable(NAME_SEARCH_PARAM_NAME, TemplateVariable.VariableType.REQUEST_PARAM)))

    return Link.of(uriTemplate, relation).withName(SEARCH)
}
