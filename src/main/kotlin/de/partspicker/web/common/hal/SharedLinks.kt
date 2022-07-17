package de.partspicker.web.common.hal

import de.partspicker.web.item.api.ItemTypeController
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Link
import org.springframework.hateoas.TemplateVariable
import org.springframework.hateoas.UriTemplate
import org.springframework.hateoas.server.mvc.linkTo

const val SIZE_PARAM_NAME = "size"
const val PAGE_PARAM_NAME = "page"

fun generateGetAllItemTypesLink(relation: String): Link {
    // READ itemTypes link with page meta information params
    val uriTemplate = UriTemplate.of(
        linkTo<ItemTypeController> { handleGetAllItemTypes(Pageable.unpaged()) }.toUri().toString()
    )
        .with(TemplateVariable(SIZE_PARAM_NAME, TemplateVariable.VariableType.REQUEST_PARAM))
        .with(TemplateVariable(PAGE_PARAM_NAME, TemplateVariable.VariableType.REQUEST_PARAM))

    return Link.of(uriTemplate, relation).withName(DefaultName.READ)
}
