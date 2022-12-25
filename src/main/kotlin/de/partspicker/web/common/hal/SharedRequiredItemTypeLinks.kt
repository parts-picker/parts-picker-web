package de.partspicker.web.common.hal // ktlint-disable filename

import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.inventory.api.RequiredItemTypeController
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.TemplateVariable
import org.springframework.hateoas.UriTemplate
import org.springframework.hateoas.server.mvc.linkTo

fun generateGetAllRequiredItemTypesLink(relation: RelationName, projectId: Long) =
    generateGetAllRequiredItemTypesLink(relation.displayName, projectId)

fun generateGetAllRequiredItemTypesLink(relation: LinkRelation, projectId: Long) =
    generateGetAllRequiredItemTypesLink(relation.toString(), projectId)

fun generateGetAllRequiredItemTypesLink(relation: String, projectId: Long): Link {
    val uriTemplate = UriTemplate.of(
        linkTo<RequiredItemTypeController> { handleGetAllByProjectId(projectId, Pageable.unpaged()) }.toUri().toString()
    )
        .with(TemplateVariable(ParamNames.SIZE_PARAM_NAME, TemplateVariable.VariableType.REQUEST_PARAM))
        .with(TemplateVariable(ParamNames.PAGE_PARAM_NAME, TemplateVariable.VariableType.REQUEST_PARAM))
        .with(TemplateVariable(ParamNames.SORT_PARAM_NAME, TemplateVariable.VariableType.REQUEST_PARAM).composite())

    return Link.of(uriTemplate, relation).withName(READ)
}
