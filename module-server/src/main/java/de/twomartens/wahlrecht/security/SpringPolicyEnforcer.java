package de.twomartens.wahlrecht.security;

import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.AuthorizationContext;
import org.keycloak.adapters.authorization.PolicyEnforcer;
import org.keycloak.adapters.authorization.spi.HttpRequest;
import org.keycloak.adapters.authorization.spi.HttpResponse;
import org.keycloak.authorization.client.ClientAuthorizationContext;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig.EnforcementMode;
import org.keycloak.representations.idm.authorization.Permission;

/**
 * Allows any request, even from anonymous, if enforcement mode is disabled. For remaining requests,
 * it refers to the original PolicyEnforcer.
 */
@Slf4j
public class SpringPolicyEnforcer {

  private final PolicyEnforcer policyEnforcer;
  private final PolicyEnforcerConfig policyEnforcerConfig;

  public SpringPolicyEnforcer(PolicyEnforcer policyEnforcer, PolicyEnforcerConfig policyEnforcerConfig) {
    this.policyEnforcer = policyEnforcer;
    this.policyEnforcerConfig = policyEnforcerConfig;
  }

  public AuthorizationContext enforce(HttpRequest request, HttpResponse response) {
    if (log.isDebugEnabled()) {
      log.debug("Policy enforcement is enabled. Enforcing policy decisions for path [{}].", request.getURI());
    }

    AuthorizationContext context = authorize(request, response);

    if (log.isDebugEnabled()) {
      log.debug("Policy enforcement result for path [{}] is : {}", request.getURI(), context.isGranted() ? "GRANTED" : "DENIED");
      log.debug("Returning authorization context with permissions:");
      for (Permission permission : context.getPermissions()) {
        log.debug(permission.toString());
      }
    }

    return context;
  }

  private AuthorizationContext authorize(HttpRequest request, HttpResponse response) {
    EnforcementMode enforcementMode = policyEnforcerConfig.getEnforcementMode();

    if (EnforcementMode.DISABLED.equals(enforcementMode)) {
      return createAuthorizedContext();
    }

    return policyEnforcer.enforce(request, response);
  }

  private AuthorizationContext createAuthorizedContext() {
    return new ClientAuthorizationContext(policyEnforcer.getAuthzClient()) {
      @Override
      public boolean hasPermission(String resourceName, String scopeName) {
        return true;
      }

      @Override
      public boolean hasResourcePermission(String resourceName) {
        return true;
      }

      @Override
      public boolean hasScopePermission(String scopeName) {
        return true;
      }

      @Override
      public List<Permission> getPermissions() {
        return Collections.emptyList();
      }

      @Override
      public boolean isGranted() {
        return true;
      }
    };
  }
}
