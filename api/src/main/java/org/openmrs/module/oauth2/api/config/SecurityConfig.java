package org.openmrs.module.oauth2.api.config;

import org.openmrs.module.oauth2.api.impl.ClientManagementControllerAuthenticationServiceImpl;
import org.openmrs.module.oauth2.api.impl.UserAuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  
//    @Autowired
//    private ClientManagementControllerAuthenticationServiceImpl clientManagementControllerAuthenticationService;

    @Autowired
    private UserAuthenticationServiceImpl userAuthenticationService;

    @Bean(name = "userAuthenticationProvider")
    public AuthenticationProvider getUserAuthenticationProvider() {
        UserAuthenticationServiceImpl authenticationService = new UserAuthenticationServiceImpl();
        authenticationService.setTypeName("Basic");
        return authenticationService;
    }
 
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(userAuthenticationService);
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
            .and().httpBasic();
    }
}