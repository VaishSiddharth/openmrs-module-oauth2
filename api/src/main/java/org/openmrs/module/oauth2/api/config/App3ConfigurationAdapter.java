package org.openmrs.module.oauth2.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@Order(3)
public class App3ConfigurationAdapter extends WebSecurityConfigurerAdapter {
 
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/ws/fhir/**").authorizeRequests().anyRequest().hasAnyRole("ROLE_USER","ROLE_CLIENT")
                .and().exceptionHandling()
                .accessDeniedHandler(getAccessDeniedHandler()).and().anonymous().disable();

        //TODO use-expressions="true" authentication-manager-ref="authenticationManager"
        // <security:custom-filter ref="OpenMRSGenericResourceServerFilter" before="PRE_AUTH_FILTER"/> not added
    }
    @Bean(name = "oauthAccessDeniedHandler")
    public AccessDeniedHandler getAccessDeniedHandler() {
        return null;//TODO return something
    }
}