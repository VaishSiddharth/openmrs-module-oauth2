package org.openmrs.module.oauth2.config;

import org.openmrs.module.oauth2.Client;
import org.openmrs.module.oauth2.api.impl.ClientManagementControllerAuthenticationServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.nio.file.AccessDeniedException;
import java.util.logging.Filter;

/*
<!-- Basic Authentication filter to supoort basic headers when making a request-->
    <bean id="basicAuthenticationFilter" class="org.springframework.security.web.authentication.www.BasicAuthenticationFilter">
        <constructor-arg ref="authenticationManager"/>
        <constructor-arg ref="clientAuthenticationEntryPoint"/>
    </bean>

    <!--To be used with token endpoint to initiate client credentials verification-->
    <bean id="clientAuthenticationEntryPoint"
          class="org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint">
        <property name="realmName" value="openmrs/client"/>
        <property name="typeName" value="Basic"/>
    </bean>
 */
@Configuration
@Order(1)
public class App1ConfigurationAdapter extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/ws/oauth/token").authorizeRequests().anyRequest().access("IS_AUTHENTICATED_FULLY").and()
		        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().httpBasic()
		        .authenticationEntryPoint(getClientAuthenticationEntryPoint()).and().exceptionHandling()
		        .accessDeniedHandler(getAccessDeniedHandler()).and().anonymous().disable()
		        .addFilterAfter(getClientControllerEndpointFilter(), BasicAuthenticationFilter.class).csrf().disable();
		//TODO use-expressions="false" not added
	}
	
	@Bean(name = "basicAuthenticationFilter")
	public AuthenticationEntryPoint authenticationEntryPoint() {
		BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
		return entryPoint;
	}
	
	@Bean(name = "clientAuthenticationEntryPoint")
	public AuthenticationEntryPoint getClientAuthenticationEntryPoint() {
		OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		authenticationEntryPoint.setRealmName("openmrs/client");
		authenticationEntryPoint.setTypeName("Basic");
		return authenticationEntryPoint;
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
	
	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean(name = "oauthAccessDeniedHandler")
	public AccessDeniedHandler getAccessDeniedHandler() {
		return null;//TODO return something
	}
}
