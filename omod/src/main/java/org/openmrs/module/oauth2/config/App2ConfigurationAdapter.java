package org.openmrs.module.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@Order(2)
public class App2ConfigurationAdapter extends WebSecurityConfigurerAdapter {
	
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/ws/oauth/authorize").httpBasic().authenticationEntryPoint(getClientAuthenticationEntryPoint())
		        .and().authorizeRequests().anyRequest().access("IS_AUTHENTICATED_FULLY")
                .and().anonymous().disable().exceptionHandling()
                .accessDeniedHandler(getAccessDeniedHandler()).and().anonymous().disable()
                .addFilterAfter(getClientControllerEndpointFilter(), BasicAuthenticationFilter.class);

        //TODO use-expressions="false" authentication-manager-ref="authenticationManager" not added

    }
	
	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean(name = "clientAuthenticationEntryPoint")
	public AuthenticationEntryPoint getClientAuthenticationEntryPoint() {
		OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		authenticationEntryPoint.setRealmName("openmrs/client");
		authenticationEntryPoint.setTypeName("Basic");
		return authenticationEntryPoint;
	}

    @Bean(name = "oauthAccessDeniedHandler")
    public AccessDeniedHandler getAccessDeniedHandler() {
        return null;//TODO return something
    }

    @Bean(name = "clientControllerEndpointFilter")
    public ClientCredentialsTokenEndpointFilter getClientControllerEndpointFilter() {
        ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter = new ClientCredentialsTokenEndpointFilter();
        try {
            clientCredentialsTokenEndpointFilter.setAuthenticationManager(authenticationManagerBean());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return clientCredentialsTokenEndpointFilter;
    }
}
