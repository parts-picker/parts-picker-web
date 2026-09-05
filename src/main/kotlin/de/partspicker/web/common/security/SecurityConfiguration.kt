package de.partspicker.web.common.security

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.actuate.health.HealthEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfiguration {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationConverter: JwtAuthenticationConverter,
        userProvisioningFilter: UserProvisioningFilter
    ): SecurityFilterChain {
        http {
            cors { }
            csrf { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authorizeHttpRequests {
                authorize(EndpointRequest.to(HealthEndpoint::class.java), permitAll)
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer {
                jwt {
                    this.jwtAuthenticationConverter = jwtAuthenticationConverter
                }
            }
            // must run once the token has been turned into an authentication
            addFilterAfter<BearerTokenAuthenticationFilter>(userProvisioningFilter)
        }
        return http.build()
    }

    @Bean
    fun jwtAuthenticationConverter(realmRoleAuthoritiesConverter: RealmRoleAuthoritiesConverter) =
        JwtAuthenticationConverter().apply {
            setJwtGrantedAuthoritiesConverter(realmRoleAuthoritiesConverter)
            setPrincipalClaimName(JwtClaims.SUBJECT)
        }
}
