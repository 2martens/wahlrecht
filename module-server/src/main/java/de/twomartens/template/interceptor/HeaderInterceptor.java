package de.twomartens.template.interceptor;

import java.io.Closeable;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;

public abstract class HeaderInterceptor {

  public static final String LOGGER_TRACE_ID = "trace.id";
  public static final String LOGGER_REQTYPE_ID = "REQTYPE";

  public static final String HEADER_FIELD_TRACE_ID = "X-TraceId";
  public static final String HEADER_FIELD_B3_TRACE_ID = "x-b3-traceid";
  public static final String HEADER_FIELD_TYPE_ID = "x-type";

  public static final String REQ_TYPE_HEALTHCHECK = "HEALTH_CHECK";
  public static final String REQ_TYPE_INTEGRATION_TEST = "INTEGRATION_TEST";
  public static final String REQ_TYPE_SERVER_TEST = "SERVER_TEST";
  public static final String REQ_TYPE_WARMUP = "WARMUP";

  public String getTraceId() {
    return Optional.ofNullable(MDC.get(LOGGER_TRACE_ID))
        .filter(s -> !s.isBlank())
        .orElse(createNewTraceId());
  }

  public Optional<String> getRequestType() {
    return Optional.ofNullable(MDC.get(LOGGER_REQTYPE_ID))
        .filter(requestType -> !requestType.isBlank());
  }

  public InterceptorCloseables setTraceId(String traceId) {
    return new InterceptorCloseables(MDC.putCloseable(LOGGER_TRACE_ID, traceId));
  }

  public InterceptorCloseables mark(String requestType) {
    return new InterceptorCloseables(MDC.putCloseable(LOGGER_REQTYPE_ID, requestType));
  }

  public InterceptorCloseables set(String traceId, String requestType) {
    return requestType != null
        ? new InterceptorCloseables(setTraceId(traceId), mark(requestType))
        : setTraceId(traceId);
  }

  public InterceptorCloseables markAsHealthCheck() {
    return new InterceptorCloseables(mark(REQ_TYPE_HEALTHCHECK), setTraceId(createNewTraceId()));
  }

  public InterceptorCloseables markAsIntegrationTest() {
    return new InterceptorCloseables(mark(REQ_TYPE_INTEGRATION_TEST), setTraceId(createNewTraceId()));
  }

  public InterceptorCloseables markAsServerTest() {
    return new InterceptorCloseables(mark(REQ_TYPE_SERVER_TEST), setTraceId(createNewTraceId()));
  }

  public InterceptorCloseables markAsWarmup() {
    return new InterceptorCloseables(mark(REQ_TYPE_WARMUP), setTraceId(createNewTraceId()));
  }

  private static String createNewTraceId() {
    return UUID.randomUUID().toString();
  }

  public static class InterceptorCloseables implements Closeable {

    private final Closeable[] closeables;

    private InterceptorCloseables(Closeable... closeables) {
      this.closeables = closeables;
    }

    @Override
    public void close() {
      for (Closeable closeable : closeables) {
        try {
          closeable.close();
        } catch (Exception ignored) {
          // do nothing
        }
      }
    }
  }

}
