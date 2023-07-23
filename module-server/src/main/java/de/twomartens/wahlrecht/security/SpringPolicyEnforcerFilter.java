package de.twomartens.wahlrecht.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.AuthorizationContext;
import org.keycloak.adapters.authorization.PolicyEnforcer;
import org.keycloak.adapters.authorization.integration.elytron.ServletHttpRequest;
import org.keycloak.adapters.authorization.integration.elytron.ServletHttpResponse;
import org.keycloak.adapters.authorization.spi.ConfigurationResolver;
import org.keycloak.adapters.authorization.spi.HttpRequest;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.lang.NonNull;

/**
 * A Jakarta Servlet {@link Filter} acting as a policy enforcer. This filter does not enforce access for anonymous subjects.</p>
 *
 * For authenticated subjects, this filter delegates the access decision to the {@link PolicyEnforcer} and decide if
 * the request should continue.</p>
 *
 * If access is not granted, this filter aborts the request and relies on the {@link PolicyEnforcer} to properly
 * respond to client.
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
@Slf4j
public class SpringPolicyEnforcerFilter implements Filter {
  private final Map<PolicyEnforcerConfig, SpringPolicyEnforcer> policyEnforcer;
  private final ConfigurationResolver configResolver;

    public SpringPolicyEnforcerFilter(ConfigurationResolver configResolver) {
      this.configResolver = configResolver;
      this.policyEnforcer = Collections.synchronizedMap(new HashMap<>());
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        ServletHttpRequest httpRequest = new ServletHttpRequest(request,
            () -> extractBearerToken(request));

        SpringPolicyEnforcer policyEnforcer = getOrCreatePolicyEnforcer(request, httpRequest);
        AuthorizationContext authzContext = policyEnforcer.enforce(httpRequest, new ServletHttpResponse(response));

        request.setAttribute(AuthorizationContext.class.getName(), authzContext);

        if (authzContext.isGranted()) {
            log.debug("Request authorized, continuing the filter chain");
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            log.debug("Unauthorized request to path [{}], aborting the filter chain", request.getRequestURI());
        }
    }

    protected String extractBearerToken(@NonNull HttpServletRequest request) {
        Enumeration<String> authorizationHeaderValues = request.getHeaders("Authorization");

        while (authorizationHeaderValues.hasMoreElements()) {
            String value = authorizationHeaderValues.nextElement();
            String[] parts = value.trim().split("\\s+");

            if (parts.length != 2) {
                continue;
            }

            String bearer = parts[0];

            if (bearer.equalsIgnoreCase("Bearer")) {
                return parts[1];
            }
        }

        return null;
    }

    private SpringPolicyEnforcer getOrCreatePolicyEnforcer(HttpServletRequest servletRequest, HttpRequest request) {
        return policyEnforcer.computeIfAbsent(configResolver.resolve(request),
            enforcerConfig -> createPolicyEnforcer(servletRequest, enforcerConfig));
    }

  protected SpringPolicyEnforcer createPolicyEnforcer(HttpServletRequest servletRequest, @NonNull PolicyEnforcerConfig enforcerConfig) {
    String authServerUrl = enforcerConfig.getAuthServerUrl();

    return new SpringPolicyEnforcer(PolicyEnforcer.builder()
        .authServerUrl(authServerUrl)
        .realm(enforcerConfig.getRealm())
        .clientId(enforcerConfig.getResource())
        .credentials(enforcerConfig.getCredentials())
        .bearerOnly(false)
        .enforcerConfig(enforcerConfig).build(), enforcerConfig);
  }
}
