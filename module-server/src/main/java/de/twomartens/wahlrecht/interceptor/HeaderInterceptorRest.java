package de.twomartens.wahlrecht.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class HeaderInterceptorRest extends HeaderInterceptor
    implements HandlerInterceptor, ClientHttpRequestInterceptor {

  public static final String CLASS_NAME = HeaderInterceptorRest.class.getName();

  // ClientHttpRequestInterceptor
  @Override
  @NonNull
  public ClientHttpResponse intercept(HttpRequest request, @NonNull byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    request.getHeaders().add(HEADER_FIELD_TRACE_ID, getTraceId());
    getRequestType().ifPresent(requestType -> request.getHeaders().add(HEADER_FIELD_TYPE_ID, requestType));
    try {
      return execution.execute(request, body);
    } finally {
      request.getHeaders().remove(HEADER_FIELD_TRACE_ID);
      request.getHeaders().remove(HEADER_FIELD_TYPE_ID);
    }
  }

  // HandlerInterceptor
  @Override
  public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull Object handler) {
    String traceId = extractTraceId(request);
    String requestType = extractRequestType(request);
    InterceptorCloseables closeable = set(traceId, requestType);
    request.setAttribute(CLASS_NAME, closeable);
    return true;
  }

  public static String extractTraceId(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(HEADER_FIELD_TRACE_ID)).filter(s -> !s.isBlank())
        .or(() -> Optional.ofNullable(request.getHeader(HEADER_FIELD_B3_TRACE_ID)).filter(s -> !s.isBlank()))
        .orElse(UUID.randomUUID().toString());
  }

  public static String extractRequestType(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(HEADER_FIELD_TYPE_ID))
        .filter(reqType -> !reqType.isBlank())
        .orElse(null);
  }

  // HandlerInterceptor
  @Override
  public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull Object handler, ModelAndView modelAndView) {
    Optional.ofNullable(request.getAttribute(CLASS_NAME))
        .filter(InterceptorCloseables.class::isInstance)
        .map(InterceptorCloseables.class::cast)
        .ifPresent(InterceptorCloseables::close);
  }
}
