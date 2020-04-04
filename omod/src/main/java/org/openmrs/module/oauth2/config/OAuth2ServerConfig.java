package org.openmrs.module.oauth2.config;

import org.openmrs.module.oauth2.api.impl.ClientDetailsServiceImpl;
import org.openmrs.module.oauth2.web.util.CustomTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
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
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
public class OAuth2ServerConfig {
	
	private static final String OPENMRS_RESOURCE_ID = "OpenMRS";

	@Qualifier("tokenServices")
	@Autowired
	private DefaultTokenServices tokenServices;
	
	@Configuration
	@EnableResourceServer
	protected class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
		//TODO <oauth:resource-server id="OpenMRSGenericResourceServerFilter"
		//                           token-services-ref="tokenServices"/>
		@Override
		public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
			resources.tokenServices(tokenServices);
			resources.resourceId(OPENMRS_RESOURCE_ID);
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
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


		@Qualifier(BeanIds.AUTHENTICATION_MANAGER)
		@Autowired
		private AuthenticationManager authenticationManager;

		@Qualifier("clientAuthenticationEntryPoint")
		@Autowired
		private OAuth2AuthenticationEntryPoint clientAuthenticationEntryPoint;

		@Qualifier("clientControllerEndpointFilter")
		@Autowired
		public ClientCredentialsTokenEndpointFilter clientControllerEndpointFilter;

		@Qualifier("userApprovalHandler")
		@Autowired
		public TokenStoreUserApprovalHandler userApprovalHandler;

		@Qualifier("oauthAccessDeniedHandler")
		@Autowired
		private OAuth2AccessDeniedHandler oauthAccessDeniedHandler;

		@Qualifier("tokenStore")
		@Autowired
		public TokenStore tokenStore;

		@Qualifier("tokenServices")
		@Autowired
		private DefaultTokenServices tokenServices;

		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			security.accessDeniedHandler(oauthAccessDeniedHandler)
					.authenticationEntryPoint(clientAuthenticationEntryPoint)
					.addTokenEndpointAuthenticationFilter(clientControllerEndpointFilter);
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.withClientDetails(clientDetailsService);
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.tokenServices(tokenServices).tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
					.authenticationManager(authenticationManager);
		}
		//TODO <oauth:authorization-server
		//                                token-endpoint-url="/oauth2/token"
		//                                user-approval-page="forward:/ws/oauth/confirm_access"
		//                                authorization-endpoint-url="/oauth2/authorize"
		//                                approval-parameter-name="user_oauth_approval"
		//                                error-page="/module/oauth2/oauth_error">
		//        <oauth:authorization-code disabled="false"/>
		//        <oauth:implicit/>
		//        <oauth:refresh-token/>
		//        <oauth:client-credentials/>
		//        <oauth:password/>
		//    </oauth:authorization-server>

	}

}
