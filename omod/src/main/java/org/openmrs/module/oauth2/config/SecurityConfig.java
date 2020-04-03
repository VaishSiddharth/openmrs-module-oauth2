package org.openmrs.module.oauth2.config;

import com.mchange.v2.c3p0.DriverManagerDataSource;
import org.openmrs.module.oauth2.api.impl.ClientAuthenticationServiceImpl;
import org.openmrs.module.oauth2.api.impl.ClientDetailsServiceImpl;
import org.openmrs.module.oauth2.api.impl.UserAuthenticationServiceImpl;
import org.openmrs.module.oauth2.web.util.CustomTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.vote.ScopeVoter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import java.util.List;

@Configuration
@ImportResource({ "classpath:applicationContext-service.xml", "classpath:openmrs-servlet.xml" })
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserAuthenticationServiceImpl userAuthenticationService;
	
	@Autowired
	private ClientAuthenticationServiceImpl clientAuthenticationService;
	
	@Bean(name = "basicAuthenticationFilter")
	public BasicAuthenticationFilter authenticationEntryPoint(AuthenticationManager authenticationManager,
	        OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint) {
		try {
			return new BasicAuthenticationFilter(authenticationManagerBean(), getClientAuthenticationEntryPoint());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		//TODO
		/*<bean id="basicAuthenticationFilter" class="org.springframework.security.web.authentication.www.BasicAuthenticationFilter">
		<constructor-arg ref="authenticationManager"/>
		<constructor-arg ref="clientAuthenticationEntryPoint"/>
		</bean>*/
	}
	
	@Bean(name = "oauthAuthenticationEntryPoint")
	public OAuth2AuthenticationEntryPoint getOauthAuthenticationEntryPoint() {
		OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		authenticationEntryPoint.setRealmName("openmrs");
		return authenticationEntryPoint;
	}
	
	@Bean(name = "clientAuthenticationEntryPoint")
	public OAuth2AuthenticationEntryPoint getClientAuthenticationEntryPoint() {
		OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		authenticationEntryPoint.setRealmName("openmrs/client");
		authenticationEntryPoint.setTypeName("Basic");
		return authenticationEntryPoint;
	}
	
	@Bean(name = "UserAuthenticationEntryPoint")
	public OAuth2AuthenticationEntryPoint getUserAuthenticationEntryPoint() {
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
	
	@Bean(name = "accessDecisionManager")
	public UnanimousBased unanimousBased(List<AccessDecisionVoter<?>> list) {
		ScopeVoter scopeVoter = new ScopeVoter();
		RoleVoter roleVoter = new RoleVoter();
		AuthenticatedVoter authenticatedVoter = new AuthenticatedVoter();
		list.add(scopeVoter);
		list.add(authenticatedVoter);
		list.add(roleVoter);
		return new UnanimousBased(list);
	}
	
	@Bean(name = "jdbcTemplate")
	public DriverManagerDataSource getJdbcTemplate() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/openmrs");
		dataSource.setUser("root");
		dataSource.setPassword("root");
		return dataSource;
	}
	
	@Bean(name = "clientDetailsUserService")
	public ClientDetailsUserDetailsService clientDetailsUserDetailsService(ClientDetailsServiceImpl clientDetailsService) {
		return new ClientDetailsUserDetailsService(clientDetailsService);
	}
	
	@Bean(name = "tokenStore")
	public JdbcTokenStore getTokenStore(DriverManagerDataSource driverManagerDataSource) {
		driverManagerDataSource = getJdbcTemplate();
		return new JdbcTokenStore(driverManagerDataSource);
	}
	@Autowired
	private CustomTokenEnhancer customTokenEnhancer;

	@Autowired
	private ClientDetailsServiceImpl clientDetailsService;

	@Bean(name = "tokenServices")
	public DefaultTokenServices getTokenServices()
	{
		DefaultTokenServices defaultTokenServices=new DefaultTokenServices();
		defaultTokenServices.setTokenStore(getTokenStore(new DriverManagerDataSource()));//TODO check
		defaultTokenServices.setSupportRefreshToken(true);
		defaultTokenServices.setClientDetailsService(clientDetailsService);
		defaultTokenServices.setTokenEnhancer(customTokenEnhancer);
		return defaultTokenServices;
	}

	@Bean(name = "userApprovalHandler")
	public TokenStoreUserApprovalHandler getUserApprovalHandler() {
		TokenStoreUserApprovalHandler tokenStoreUserApprovalHandler = new TokenStoreUserApprovalHandler();
		tokenStoreUserApprovalHandler.setTokenStore(getTokenStore(new DriverManagerDataSource()));//TODO check
		return tokenStoreUserApprovalHandler;
		//TODO I think xml mapping is wrong no such class TokenServicesUserApprovalHandler
		// <bean id="userApprovalHandler"
		//          class="org.springframework.security.oauth2.provider.approval.TokenServicesUserApprovalHandler">
		//        <property name="tokenServices" ref="tokenServices"/>
		//    </bean>
	}
	
	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean(name = "oauthAccessDeniedHandler")
	public OAuth2AccessDeniedHandler getAccessDeniedHandler() {
		return new OAuth2AccessDeniedHandler();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(userAuthenticationService);
		auth.authenticationProvider(clientAuthenticationService);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//TODO how to map authentication-manager-ref="authenticationManager"
		//                   use-expressions="true">
		//<security:intercept-url pattern="/oauth/clientManagement"
		http.antMatcher("/ws/oauth/clientManagement").sessionManagement()
		        .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests().anyRequest()
		        .access("IS_AUTHENTICATED_FULLY").and().httpBasic().and().csrf().disable();
		
		http.antMatcher("/ws/oauth/metadata").authorizeRequests().anyRequest().permitAll();
		
		//TODO authentication-manager-ref="clientAuthenticationManager"
		//                   use-expressions="false">
		//        <security:intercept-url pattern="/oauth/token"
		http.antMatcher("/ws/oauth/token").sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		        .authorizeRequests().anyRequest().access("IS_AUTHENTICATED_FULLY").and().anonymous().disable().httpBasic()
		        .authenticationEntryPoint(getClientAuthenticationEntryPoint()).and()
		        .addFilterAfter(getClientControllerEndpointFilter(), BasicAuthenticationFilter.class).exceptionHandling()
		        .accessDeniedHandler(getAccessDeniedHandler()).and().csrf().disable();
		
		//TODO authentication-manager-ref="authenticationManager"
		//                   use-expressions="false">
		//        <security:intercept-url pattern="/oauth/authorize"
		http.antMatcher("/ws/oauth/authorize").authorizeRequests().anyRequest().access("IS_AUTHENTICATED_FULLY").and()
		        .anonymous().disable().httpBasic().authenticationEntryPoint(getUserAuthenticationEntryPoint()).and()
		        .addFilterAfter(getClientControllerEndpointFilter(), BasicAuthenticationFilter.class).exceptionHandling()
		        .accessDeniedHandler(getAccessDeniedHandler());
		
		//TODO use-expressions="true" authentication-manager-ref="authenticationManager"
		// <security:custom-filter ref="OpenMRSGenericResourceServerFilter" before="PRE_AUTH_FILTER"/> not added
		http.antMatcher("/ws/fhir/**").httpBasic().authenticationEntryPoint(getOauthAuthenticationEntryPoint()).and()
		        .authorizeRequests().anyRequest().hasAnyRole("ROLE_USER", "ROLE_CLIENT").and()
		        .addFilterAfter(getClientControllerEndpointFilter(), AbstractPreAuthenticatedProcessingFilter.class)
		        .exceptionHandling().accessDeniedHandler(getAccessDeniedHandler()).and().anonymous().disable();
		
		//TODO  xmlns="http://www.springframework.org/schema/security"
		//          use-expressions="false">
		//        <access-denied-handler error-page="/referenceapplication/login.page?authorization_error=true"/>
		//        <!--access-denied-handler error-page="/login.htm?authorization_error=true"/-->
		//        <intercept-url pattern="/ws/oauth/**" access="IS_AUTHENTICATED_ANONYMOUSLY"/>
		//        <intercept-url pattern="/ws/**" access="IS_AUTHENTICATED_ANONYMOUSLY"/>
		//        <intercept-url pattern="/ms/**" access="IS_AUTHENTICATED_ANONYMOUSLY"/>
		//        <intercept-url pattern="/login*" access="IS_AUTHENTICATED_ANONYMOUSLY"/>
		//        <http-basic/>
		//        <headers disabled="false"/>
		http.sessionManagement().enableSessionUrlRewriting(false).and().headers().disable().httpBasic().and().formLogin()
		        .loginPage("/login.htm").defaultSuccessUrl("/referenceapplication/home.page")
		        .loginProcessingUrl("/login.htm").failureUrl("/referenceapplication/login.page").and().csrf().disable();
		
	}
}
