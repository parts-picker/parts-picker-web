package de.partspicker.web.common.hal

import org.springframework.hateoas.TemplateVariable
import org.springframework.hateoas.UriTemplate

fun UriTemplate.withPaginationParams() = this
    .with(TemplateVariable(ParamNames.SIZE_PARAM_NAME, TemplateVariable.VariableType.REQUEST_PARAM))
    .with(TemplateVariable(ParamNames.PAGE_PARAM_NAME, TemplateVariable.VariableType.REQUEST_PARAM))
    .with(TemplateVariable(ParamNames.SORT_PARAM_NAME, TemplateVariable.VariableType.REQUEST_PARAM).composite())
