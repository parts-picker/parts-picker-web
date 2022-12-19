package de.partspicker.web.common.hal // ktlint-disable filename

import de.partspicker.web.common.hal.ParamNames.Companion.PAGE_PARAM_NAME
import de.partspicker.web.common.hal.ParamNames.Companion.SIZE_PARAM_NAME
import de.partspicker.web.common.hal.ParamNames.Companion.SORT_PARAM_NAME
import de.partspicker.web.project.api.ProjectController
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.TemplateVariable
import org.springframework.hateoas.UriTemplate
import org.springframework.hateoas.server.mvc.linkTo

fun generateGetAllProjectsLink(relation: LinkRelation) = generateGetAllProjectsLink(relation.toString())

fun generateGetAllProjectsLink(relation: String): Link {
    // READ projects link with page meta information params
    val uriTemplate = UriTemplate.of(
        linkTo<ProjectController> { handleGetAllProjects(Pageable.unpaged()) }.toUri().toString()
    )
        .with(TemplateVariable(SIZE_PARAM_NAME, TemplateVariable.VariableType.REQUEST_PARAM))
        .with(TemplateVariable(PAGE_PARAM_NAME, TemplateVariable.VariableType.REQUEST_PARAM))
        .with(TemplateVariable(SORT_PARAM_NAME, TemplateVariable.VariableType.REQUEST_PARAM).composite())

    return Link.of(uriTemplate, relation).withName(DefaultName.READ)
}
