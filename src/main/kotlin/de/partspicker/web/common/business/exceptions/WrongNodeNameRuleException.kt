package de.partspicker.web.common.business.exceptions

class WrongNodeNameRuleException(actualNodeName: String?, expectedNodeName: String) :
    RuleException("Expected project status is $expectedNodeName, but actual status is $actualNodeName")
