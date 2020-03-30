package org.openmrs.module.oauth2.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.module.oauth2.api.impl.ClientAuthenticationServiceImpl;
import org.openmrs.module.oauth2.web.controller.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public ContentNegotiatingViewResolver contentViewResolver() throws Exception {
		ContentNegotiationManagerFactoryBean contentNegotiationManager = new ContentNegotiationManagerFactoryBean();
		contentNegotiationManager.addMediaType("json", MediaType.APPLICATION_JSON);
		
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/");
		viewResolver.setSuffix(".jsp");
		
		MappingJackson2JsonView defaultView = new MappingJackson2JsonView();
		defaultView.setExtractValueFromSingleKeyModel(true);
		
		ContentNegotiatingViewResolver contentViewResolver = new ContentNegotiatingViewResolver();
		contentViewResolver.setContentNegotiationManager(contentNegotiationManager.getObject());
		contentViewResolver.setViewResolvers(Arrays.<ViewResolver> asList(viewResolver));
		contentViewResolver.setDefaultViews(Arrays.<View> asList(defaultView));
		return contentViewResolver;
	}

	@Bean
	public AccessConfirmationController accessConfirmationController(ClientDetailsService clientDetailsService) {
		AccessConfirmationController accessConfirmationController = new AccessConfirmationController();
		accessConfirmationController.setClientDetailsService(clientDetailsService);
		return accessConfirmationController;
	}

	@Bean
	public ClientManagementController clientManagementController() {
		ClientManagementController clientManagementController = new ClientManagementController();
		return clientManagementController;
	}

	@Bean
	public ClientRegistrationFormController clientRegistrationFormController() {
		ClientRegistrationFormController clientRegistrationFormController = new ClientRegistrationFormController();
		return clientRegistrationFormController;
	}

	@Bean
	public MetadataController metadataController() {
		MetadataController metadataController = new MetadataController();
		return metadataController;
	}

	@Bean
	public OmodBypassController omodBypassController() {
		OmodBypassController omodBypassController = new OmodBypassController();
		return omodBypassController;
	}

	@Bean
	public RegisteredClientIndexController registeredClientIndexController() {
		RegisteredClientIndexController registeredClientIndexController = new RegisteredClientIndexController();
		return registeredClientIndexController;
	}

	@Bean
	public RunSmartAppController runSmartAppController() {
		RunSmartAppController runSmartAppController = new RunSmartAppController();
		return runSmartAppController;
	}

	@Bean
	public ViewEditRegisteredClientFormController viewEditRegisteredClientFormController() {
		ViewEditRegisteredClientFormController viewEditRegisteredClientFormController = new ViewEditRegisteredClientFormController();
		return viewEditRegisteredClientFormController;
	}

	//TODO Service impl bean should be created here?

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
