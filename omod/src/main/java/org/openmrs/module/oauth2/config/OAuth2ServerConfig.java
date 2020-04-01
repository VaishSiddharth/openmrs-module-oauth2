package org.openmrs.module.oauth2.config;

import com.mchange.v2.c3p0.DriverManagerDataSource;

import org.openmrs.module.oauth2.api.impl.ClientDetailsServiceImpl;
import org.openmrs.module.oauth2.web.util.CustomTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import sun.tools.jstat.Token;

import javax.sql.DataSource;

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
			http.httpBasic().authenticationEntryPoint(getOauthAuthenticationEntryPoint());
			super.configure(http);
		}
	}
	
	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
		
		@Autowired
		private CustomTokenEnhancer customTokenEnhancer;
		
		@Autowired
		private ClientDetailsServiceImpl clientDetailsService;
		
		/*TODO here do we have to setup something like 
		<authentication-manager id="clientAuthenticationManager" xmlns="http://www.springframework.org/schema/security">
		        <authentication-provider ref="clientAuthenticationProvider"/>
		        <!--user-service-ref="clientDetailsUserService"/>-->
		    </authentication-manager>
		    <bean id="clientAuthenticationProvider" class="org.openmrs.module.oauth2.api.impl.ClientAuthenticationServiceImpl"/>*/

		@Autowired
		@Qualifier("authenticationManager")
		private AuthenticationManager authenticationManager;
		
		@Bean(name = "clientAuthenticationEntryPoint")
		public AuthenticationEntryPoint getClientAuthenticationEntryPoint() {
			OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
			authenticationEntryPoint.setRealmName("openmrs/client");
			authenticationEntryPoint.setTypeName("Basic");
			return authenticationEntryPoint;
		}
		
		@Bean(name = "clientControllerAuthenticationManager")
		public ClientCredentialsTokenEndpointFilter getClientControllerAuthenticationManager() {
			ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter = new ClientCredentialsTokenEndpointFilter();
			clientCredentialsTokenEndpointFilter.setAuthenticationManager(authenticationManager);
			return clientCredentialsTokenEndpointFilter;
		}
		
		@Bean(name = "jdbcTemplate")
		public DataSource getJdbcTemplate() {
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClass("com.mysql.jdbc.Driver");
			dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/openmrs");
			dataSource.setUser("root");
			dataSource.setPassword("root");
			return dataSource;
		}
		
		@Bean(name = "tokenServices")
		public DefaultTokenServices getTokenServices() {
			DefaultTokenServices tokenServices = new DefaultTokenServices();
			tokenServices.setTokenStore(getTokenStore());
			tokenServices.setSupportRefreshToken(true);
			tokenServices.setClientDetailsService(clientDetailsService);
			tokenServices.setTokenEnhancer(customTokenEnhancer);
			return tokenServices;
		}
		
		@Bean(name = "userApprovalHandler")
		public TokenStoreUserApprovalHandler getUserApprovalHandler() {
			TokenStoreUserApprovalHandler tokenStoreUserApprovalHandler = new TokenStoreUserApprovalHandler();
			tokenStoreUserApprovalHandler.setTokenStore(getTokenStore());
			return tokenStoreUserApprovalHandler;
			//TODO I think xml mapping is wrong no such class TokenServicesUserApprovalHandler
			// <bean id="userApprovalHandler"
			//          class="org.springframework.security.oauth2.provider.approval.TokenServicesUserApprovalHandler">
			//        <property name="tokenServices" ref="tokenServices"/>
			//    </bean>
		}
		
		@Bean(name = "oauthAccessDeniedHandler")
		public OAuth2AccessDeniedHandler getOauthAccessDeniedHandler() {
			return new OAuth2AccessDeniedHandler();
		}
		
		@Bean(name = "tokenStore")
		public TokenStore getTokenStore() {
			return new JdbcTokenStore(getJdbcTemplate());
		}
		
		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			security.accessDeniedHandler(getOauthAccessDeniedHandler())
			        .authenticationEntryPoint(getClientAuthenticationEntryPoint())
			        .addTokenEndpointAuthenticationFilter(getClientControllerAuthenticationManager());
		}
		
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.withClientDetails(clientDetailsService);
		}
		
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.tokenStore(getTokenStore()).userApprovalHandler(getUserApprovalHandler())
			        .authenticationManager(authenticationManager);
		}
	}
	
}
