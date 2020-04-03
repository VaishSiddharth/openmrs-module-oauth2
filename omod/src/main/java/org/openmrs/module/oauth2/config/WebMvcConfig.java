package org.openmrs.module.oauth2.config;

import java.util.List;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages ="org.openmrs.web.controller")
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	public WebMvcConfig() {
		super();
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		super.addFormatters(registry);
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		ResourceHttpMessageConverter resourceHttpMessageConverter=new ResourceHttpMessageConverter();
		Jaxb2RootElementHttpMessageConverter jaxb2RootElementHttpMessageConverter=new Jaxb2RootElementHttpMessageConverter();
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter=new MappingJackson2HttpMessageConverter();
		converters.add(resourceHttpMessageConverter);
		converters.add(jaxb2RootElementHttpMessageConverter);
		converters.add(mappingJackson2HttpMessageConverter);
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.extendMessageConverters(converters);
	}

	@Override
	public Validator getValidator() {
		return super.getValidator();
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		super.configureContentNegotiation(configurer);
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		super.configureAsyncSupport(configurer);
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		super.configurePathMatch(configurer);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		super.addArgumentResolvers(argumentResolvers);
	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		super.addReturnValueHandlers(returnValueHandlers);
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		super.configureHandlerExceptionResolvers(exceptionResolvers);
	}

	@Override
	public MessageCodesResolver getMessageCodesResolver() {
		return super.getMessageCodesResolver();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		super.addInterceptors(registry);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		super.addViewControllers(registry);
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		super.configureViewResolvers(registry);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		super.addResourceHandlers(registry);
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		super.configureDefaultServletHandling(configurer);
	}
}
