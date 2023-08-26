package de.partspicker.web.workflow.business

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser

@Configuration
class SpELConfig {

    @Bean
    fun expressionParser(): ExpressionParser {
        return SpelExpressionParser()
    }
}
