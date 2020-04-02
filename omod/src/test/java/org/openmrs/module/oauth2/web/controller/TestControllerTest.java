package org.openmrs.module.oauth2.web.controller;

import ca.uhn.hl7v2.app.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.oauth2.config.MethodSecurityConfig;
import org.openmrs.module.oauth2.config.WebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class, MethodSecurityConfig.class, WebMvcConfig.class, WebMvcConfig.class})
@WebAppConfiguration
public class TestControllerTest {
	
	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	@Autowired
	private FilterChainProxy filterChainProxy;
	
	//    @Before
	//    public void setup() {
	//        mockMvc = webAppContextSetup(wac)
	//                .apply() //will perform all of the initial setup to integrate Spring Security with Spring MVC Test
	//                .build();
	//    }
	//    @Before
	//    public void setUp() {
	//        MockitoAnnotations.initMocks(this);
	//        this.mockMvc = webAppContextSetup(wac).dispatchOptions(true).build();
	//    }
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = webAppContextSetup(wac).dispatchOptions(true).addFilters(filterChainProxy).build();
	}
	
	@Test
	public void getTest() throws Exception {
		mockMvc.perform(get("/ws/oauth")).andExpect(status().isForbidden());
	}
	
}
