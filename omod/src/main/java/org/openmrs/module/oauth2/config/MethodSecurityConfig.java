package org.openmrs.module.oauth2.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

  /*TODO set these
    <oauth:expression-handler id="oauthExpressionHandler"/>
    <oauth:web-expression-handler id="oauthWebExpressionHandler"/>
   */
  @Override
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    return super.createExpressionHandler();
  }

  @Override
  public void setMethodSecurityExpressionHandler(List<MethodSecurityExpressionHandler> handlers) {
    super.setMethodSecurityExpressionHandler(handlers);
  }

}
