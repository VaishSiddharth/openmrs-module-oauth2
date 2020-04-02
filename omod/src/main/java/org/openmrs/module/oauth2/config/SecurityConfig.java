package org.openmrs.module.oauth2.config;

import org.openmrs.module.oauth2.api.impl.ClientAuthenticationServiceImpl;
import org.openmrs.module.oauth2.api.impl.UserAuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserAuthenticationServiceImpl userAuthenticationService;
	
	@Autowired
	private ClientAuthenticationServiceImpl clientAuthenticationService;
	
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
		return new OAuth2AccessDeniedHandler();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(userAuthenticationService).authenticationProvider(clientAuthenticationService);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/ws/oauth/token").authorizeRequests().anyRequest().access("IS_AUTHENTICATED_FULLY").and()
		        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().httpBasic()
		        .authenticationEntryPoint(getClientAuthenticationEntryPoint()).and().exceptionHandling()
		        .accessDeniedHandler(getAccessDeniedHandler()).and().anonymous().disable()
		        .addFilterAfter(getClientControllerEndpointFilter(), BasicAuthenticationFilter.class).csrf().disable();
		
		http.antMatcher("/ws/oauth/authorize").httpBasic().authenticationEntryPoint(getClientAuthenticationEntryPoint())
		        .and().authorizeRequests().anyRequest().access("IS_AUTHENTICATED_FULLY").and().anonymous().disable()
		        .exceptionHandling().accessDeniedHandler(getAccessDeniedHandler()).and().anonymous().disable()
		        .addFilterAfter(getClientControllerEndpointFilter(), BasicAuthenticationFilter.class);
		
		http.antMatcher("/ws/fhir/**").authorizeRequests().anyRequest().hasAnyRole("ROLE_USER", "ROLE_CLIENT").and()
		        .exceptionHandling().accessDeniedHandler(getAccessDeniedHandler()).and().anonymous().disable();
	}
}
