package org.openmrs.module.oauth2.api.config;

import org.openmrs.module.oauth2.Client;
import org.openmrs.module.oauth2.api.impl.ClientDetailsServiceImpl;
import org.openmrs.module.oauth2.api.impl.ClientManagementControllerAuthenticationServiceImpl;
import org.openmrs.module.oauth2.api.impl.UserAuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
public class OAuth2ServerConfig {
	
	private static final String OPENMRS_RESOURCE_ID = "OpenMRS";

	@Bean(name = "oauthAuthenticationEntryPoint")
	public AuthenticationEntryPoint getOauthAuthenticationEntryPoint() {
		OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		authenticationEntryPoint.setRealmName("openmrs");
		return authenticationEntryPoint;
	}
	
	@Configuration
	@EnableResourceServer
	protected class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
		
		@Override
		public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
			resources.resourceId(OPENMRS_RESOURCE_ID).stateless(false);
		}
		
		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.httpBasic().authenticationEntryPoint(getOauthAuthenticationEntryPoint());//TODO why not getClientAuthenticationEntryPoint
			super.configure(http);
		}
	}

	@Bean(name = "clientAuthenticationEntryPoint")
	public AuthenticationEntryPoint getClientAuthenticationEntryPoint() {
		OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		authenticationEntryPoint.setRealmName("openmrs/client");
		authenticationEntryPoint.setTypeName("Basic");
		return authenticationEntryPoint;
	}

	@Bean(name = "userAuthenticationProvider")
	public AuthenticationEntryPoint getUserAuthenticationProvider() {
		OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		authenticationEntryPoint.setRealmName("openmrs/client");
		authenticationEntryPoint.setTypeName("Basic");
		return authenticationEntryPoint;
	}
	
	@Bean(name = "clientControllerAuthenticationProvider")
	public AuthenticationProvider getClientControllerAuthenticationProvider() {
		return new ClientManagementControllerAuthenticationServiceImpl();
	}

	@Bean(name = "clientControllerAuthenticationManager")
	public AuthenticationManager getClientControllerAuthenticationManager() {
		return null;//TODO what do return here?
	}
	
	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
		
		@Autowired
		private TokenStore tokenStore;//TODO change with JdbcTokenStore after creating a component (not sure)
		
		@Autowired
		private ClientDetailsServiceImpl clientDetailsService; //TODO add @Component to ClientDetailsServiceImpl
		
		@Autowired
		private UserApprovalHandler userApprovalHandler;
		
		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;//TODO Client & User Authentication Service impl present
		
		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			super.configure(security);
		}
		
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.withClientDetails(clientDetailsService);
		}
		
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
			        .authenticationManager(authenticationManager);
		}
	}
	
}
