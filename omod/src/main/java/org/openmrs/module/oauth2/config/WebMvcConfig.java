package org.openmrs.module.oauth2.config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.persistence.XmlArrayList;
import org.openmrs.web.OpenmrsBindingInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.http.MediaType;

import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.security.access.method.P;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;
import org.springframework.web.servlet.mvc.multiaction.ParameterMethodNameResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.XmlViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.xml.MarshallingView;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages ="org.openmrs.web.controller")
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	
	@Bean(name = "jspViewResolver")
	public InternalResourceViewResolver jspViewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/view/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}
	
	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
		commonsMultipartResolver.setMaxUploadSize(75000000);
		return commonsMultipartResolver;
	}
	
	@Bean(name = "xStreamMarshaller")
	public XStreamMarshaller xStreamMarshaller() {
		return new XStreamMarshaller();
	}
	
	@Bean
	public ContentNegotiatingViewResolver contentViewResolver() throws Exception {
		ContentNegotiationManagerFactoryBean contentNegotiationManager = new ContentNegotiationManagerFactoryBean();
		contentNegotiationManager.addMediaType("json", MediaType.APPLICATION_JSON);
		contentNegotiationManager.addMediaType("xml", MediaType.APPLICATION_XML);
		
		MappingJackson2JsonView defaultView = new MappingJackson2JsonView();
		defaultView.setExtractValueFromSingleKeyModel(true);
		
		MarshallingView marshallingView = new MarshallingView();
		marshallingView.setMarshaller(xStreamMarshaller());
		
		ContentNegotiatingViewResolver contentViewResolver = new ContentNegotiatingViewResolver();
		contentViewResolver.setContentNegotiationManager(contentNegotiationManager.getObject());
		contentViewResolver.setDefaultViews(Arrays.<View> asList(defaultView, marshallingView));
		return contentViewResolver;
	}
	
	@Bean(name = "xmlMarshallingHttpMessageConverter")
	public MarshallingHttpMessageConverter xmlMarshallingHttpMessageConverter() {
		MarshallingHttpMessageConverter marshallingHttpMessageConverter = new MarshallingHttpMessageConverter();
		marshallingHttpMessageConverter.setMarshaller(xStreamMarshaller());
		marshallingHttpMessageConverter.setUnmarshaller(xStreamMarshaller());
		return marshallingHttpMessageConverter;
	}
	
	@Bean(name = "defaultMethodParamResolver")
	public ParameterMethodNameResolver defaultMethodParamResolver() {
		ParameterMethodNameResolver parameterMethodNameResolver = new ParameterMethodNameResolver();
		parameterMethodNameResolver.setParamName("method");
		parameterMethodNameResolver.setDefaultMethodName("handleRequest");
		return parameterMethodNameResolver;
	}
	
	@Bean
	public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
		Properties mapping = new Properties();
		mapping.setProperty("org.openmrs.api.APIException", "uncaughtException");
		
		SimpleMappingExceptionResolver simpleMappingExceptionResolver = new SimpleMappingExceptionResolver();
		simpleMappingExceptionResolver.setExceptionMappings(mapping);
		simpleMappingExceptionResolver.setOrder(100);
		return simpleMappingExceptionResolver;
	}
	
	@Bean
	public DefaultAnnotationHandlerMapping defaultAnnotationHandlerMapping() {
		DefaultAnnotationHandlerMapping handlerMapping = new DefaultAnnotationHandlerMapping();
		handlerMapping.setOrder(98);
		return handlerMapping;
	}
	
	@Bean
	public SimpleControllerHandlerAdapter simpleControllerHandlerAdapter() {
		return new SimpleControllerHandlerAdapter();
	}
	
	@Autowired
	public OpenmrsBindingInitializer openmrsBindingInitializer;
	//	@Bean
	//	public OpenmrsBindingInitializer openmrsBindingInitializer(){
	//		return new OpenmrsBindingInitializer();
	//	}
	
	@Bean
	public AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter() {
		ByteArrayHttpMessageConverter byteArrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
		SourceHttpMessageConverter sourceHttpMessageConverter = new SourceHttpMessageConverter();
		MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
		
		AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
		annotationMethodHandlerAdapter.setWebBindingInitializer(openmrsBindingInitializer);
		annotationMethodHandlerAdapter.setMessageConverters(
		    new HttpMessageConverter[] { byteArrayHttpMessageConverter, stringHttpMessageConverter, formHttpMessageConverter,
		            sourceHttpMessageConverter, mappingJacksonHttpMessageConverter, xmlMarshallingHttpMessageConverter() });
		return annotationMethodHandlerAdapter;
	}

	@Bean(name = "conversion-service")
	public FormattingConversionServiceFactoryBean formattingConversionServiceFactoryBean()
	{
		return new FormattingConversionServiceFactoryBean();
	}

	@Bean(name = "urlMapping")
	public SimpleUrlHandlerMapping simpleUrlHandlerMapping()
	{
		SimpleUrlHandlerMapping simpleUrlHandlerMapping=new SimpleUrlHandlerMapping();
		simpleUrlHandlerMapping.setOrder(99);
		return simpleUrlHandlerMapping;
	}

	//TODO <alias name="messageSourceServiceTarget" alias="messageSource"/>
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
