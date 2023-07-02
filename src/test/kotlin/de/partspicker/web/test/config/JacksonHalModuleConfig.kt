package de.partspicker.web.test.config

import com.fasterxml.jackson.databind.Module
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.hateoas.mediatype.MessageResolver
import org.springframework.hateoas.mediatype.hal.CurieProvider
import org.springframework.hateoas.mediatype.hal.HalConfiguration
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule
import org.springframework.hateoas.server.LinkRelationProvider
import org.springframework.hateoas.server.core.AnnotationLinkRelationProvider
import org.springframework.hateoas.server.core.DelegatingLinkRelationProvider
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

/**
 * Enables the deserialization of HAL resources  for testing.
 */
@Configuration
class JacksonHalModuleConfig {
    @Bean
    fun halModule(): Module = Jackson2HalModule()

    @Bean
    fun jsonCustomizer(configuration: HalConfiguration): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
            val provider: LinkRelationProvider = DelegatingLinkRelationProvider(AnnotationLinkRelationProvider())
            builder.handlerInstantiator(
                Jackson2HalModule.HalHandlerInstantiator(
                    provider,
                    CurieProvider.NONE,
                    MessageResolver.DEFAULTS_ONLY,
                    configuration,
                    DefaultListableBeanFactory()
                )
            )
        }
    }
}
