package de.twomartens.wahlrecht.interceptor;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager.Log4jMarker;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Log4j2
@RequiredArgsConstructor
public class LoggingInterceptorRest implements Filter, ClientHttpRequestInterceptor {

  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
  public static final Marker MARKER = new Log4jMarker("communication");

  private static final int MAX_LOG_SIZE = 20480; // 20 KB - logging could fail with bigger logmessages

  public static final String DIRECTION_IN = "inbound";
  public static final String DIRECTION_OUT = "outbound";
  public static final String PROTOCOL_NAME = "http";

  public static final String PARAM_URL_FULL = "url.full";
  public static final String PARAM_URL_DOMAIN = "url.domain";
  public static final String PARAM_URL_EXTENSION = "url.extension";
  public static final String PARAM_URL_PATH = "url.path";
  public static final String PARAM_URL_PORT = "url.port";
  public static final String PARAM_URL_SCHEME = "url.scheme";
  public static final String PARAM_URL_QUERY = "url.query";
  public static final String PARAM_BUSINESS_TYPE = "http.request.type";
  public static final String PARAM_REQUEST_BODY = "http.request.body.content";
  public static final String PARAM_RESPONSE_BODY = "http.response.body.content";
  public static final String PARAM_RESPONSE_STATUS = "http.response.status_code";
  public static final String PARAM_REQUEST_HEADERS = "http.request.headers";
  public static final String PARAM_RESPONSE_HEADERS = "http.response.headers";
  public static final String PARAM_REQUEST_BYTES = "http.request.body.bytes";
  public static final String PARAM_RESPONSE_BYTES = "http.response.body.bytes";
  public static final String PARAM_REQUEST_MIMETYPE = "http.request.mime_type";
  public static final String PARAM_RESPONSE_MIMETYPE = "http.response.mime_type";
  public static final String PARAM_REQUEST_METHOD = "http.request.method";
  public static final String PARAM_REQUEST_REFERER = "http.request.referrer";
  public static final String PARAM_REQUEST_TIME = "event.start";
  public static final String PARAM_RESPONSE_TIME = "event.end";
  public static final String PARAM_DURATION = "event.duration";
  public static final String PARAM_USER_AGENT = "user_agent.original";
  public static final String PARAM_DIRECTION = "network.direction";
  public static final String PARAM_PROTOCOL = "network.protocol";

  private final FieldLogBehaviour requestLogBehaviour;
  private final FieldLogBehaviour responseLogBehaviour;
  private final Clock clock;

  public LoggingInterceptorRest(Clock clock) {
    this(FieldLogBehaviour.NEVER, FieldLogBehaviour.NEVER, clock);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    ZonedDateTime requestTime = ZonedDateTime.now(clock);
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
    ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(
        (HttpServletResponse) response);

    byte[] fullResponseBytes = null;
    Throwable throwable = null;
    String responseBody = null;
    int httpStatusCode = -1;

    try {
      try {
        chain.doFilter(
            requestLogBehaviour != FieldLogBehaviour.NEVER ? requestWrapper : httpRequest,
            responseWrapper);
        if (responseLogBehaviour != FieldLogBehaviour.NEVER) {
          fullResponseBytes = responseWrapper.getContentAsByteArray();
        }
        httpStatusCode = responseWrapper.getStatus();
      } finally {
        responseWrapper.copyBodyToResponse();
      }
    } catch (Exception e) {
      throwable = e;
      throw e;
    } finally {
      try {
        int responseSize = responseWrapper.getContentSize();
        Map<String, Collection<String>> responseHeaders = extractHeaders(
            responseWrapper.getHeaderNames().iterator(),
            s -> responseWrapper.getHeaders(s).iterator());
        if ((responseLogBehaviour == FieldLogBehaviour.ONLY_ON_ERROR && isError(httpStatusCode)
            || responseLogBehaviour == FieldLogBehaviour.ALWAYS) && fullResponseBytes != null) {
          responseBody = new String(fullResponseBytes,
              determineResponseEncoding(responseHeaders, fullResponseBytes));
        }
        URL requestUrl = new URL(Optional.ofNullable(httpRequest.getQueryString())
            .map(qs -> httpRequest.getRequestURL().toString() + "?" + qs)
            .orElse(httpRequest.getRequestURL().toString()));
        Map<String, Collection<String>> requestHeaders = extractHeaders(
            httpRequest.getHeaderNames().asIterator(),
            s -> httpRequest.getHeaders(s).asIterator());
        String requestBody = null;
        String businessType = null;
        if (requestLogBehaviour == FieldLogBehaviour.ONLY_ON_ERROR && isError(httpStatusCode)
            || requestLogBehaviour == FieldLogBehaviour.ALWAYS) {
          byte[] fullRequestBytes = requestWrapper.getContentAsByteArray();
          if (fullRequestBytes != null) {
            requestBody = new String(fullRequestBytes,
                determineRequestEncoding(requestHeaders, fullRequestBytes));
          }
          businessType = determineBusinessType(requestUrl, requestBody);
        }
        log(LogMessage.builder()
            .requestHeaders(requestHeaders)
            .responseHeaders(responseHeaders)
            .url(requestUrl)
            .method(httpRequest.getMethod())
            .requestMimeType(typeToString(request.getContentType()))
            .responseMimeType(typeToString(response.getContentType()))
            .requestBody(requestBody)
            .responseBody(responseBody)
            .requestSize(httpRequest.getContentLength())
            .responseSize(responseSize)
            .httpStatus(httpStatusCode)
            .direction(DIRECTION_IN)
            .requestTime(requestTime)
            .responseTime(ZonedDateTime.now(clock))
            .traceId(HeaderInterceptorRest.extractTraceId(httpRequest))
            .requestType(HeaderInterceptorRest.extractRequestType(httpRequest))
            .businessType(businessType)
            .throwable(throwable)
            .build());
      } catch (RuntimeException e) {
        log.error(e.toString(), e);
      }
    }
  }

  private boolean isError(int httpStatusCode) {
    return httpStatusCode >= 400 && httpStatusCode < 600;
  }

  @NonNull
  @Override
  public ClientHttpResponse intercept(@NonNull HttpRequest request,
      @NonNull byte[] requestBytes, @NonNull ClientHttpRequestExecution execution)
      throws IOException {

    ZonedDateTime requestTime = ZonedDateTime.now(clock);
    int responseSize = 0;
    Map<String, Collection<String>> responseHeaders = Collections.emptyMap();
    MediaType responseMediaType = null;
    int httpStatusCode = -1;
    Throwable throwable = null;
    String requestBody = null;
    String responseBody = null;
    String businessType = null;

    try {
      BufferingClientHttpResponseWrapper result = new BufferingClientHttpResponseWrapper(
          execution.execute(request, requestBytes));
      byte[] responseBytes = StreamUtils.copyToByteArray(result.getBody());
      responseSize = responseBytes.length;
      responseHeaders = extractHeaders(result.getHeaders());
      responseMediaType = result.getHeaders().getContentType();
      httpStatusCode = result.getStatusCode().value();
      if (responseLogBehaviour == FieldLogBehaviour.ONLY_ON_ERROR && isError(httpStatusCode)
          || responseLogBehaviour == FieldLogBehaviour.ALWAYS) {
        responseBody = new String(responseBytes,
            determineRequestEncoding(responseHeaders, responseBytes));
      }
      return result;
    } catch (Exception e) {
      throwable = e;
      throw e;
    } finally {
      try {
        URL url = request.getURI().toURL();
        Map<String, Collection<String>> requestHeaders = extractHeaders(request.getHeaders());
        if (requestLogBehaviour == FieldLogBehaviour.ONLY_ON_ERROR && isError(httpStatusCode)
            || requestLogBehaviour == FieldLogBehaviour.ALWAYS) {
          requestBody = new String(requestBytes,
              determineRequestEncoding(requestHeaders, requestBytes));
          businessType = determineBusinessType(url, requestBody);
        }
        log(LogMessage.builder()
            .requestHeaders(requestHeaders)
            .responseHeaders(responseHeaders)
            .url(url)
            .method(Objects.requireNonNull(request.getMethod()).toString())
            .requestMimeType(typeToString(request.getHeaders().getContentType()))
            .requestBody(requestBody)
            .responseBody(responseBody)
            .responseMimeType(typeToString(responseMediaType))
            .requestSize(requestBytes.length)
            .responseSize(responseSize)
            .httpStatus(httpStatusCode)
            .direction(DIRECTION_OUT)
            .requestTime(requestTime)
            .responseTime(ZonedDateTime.now(clock))
            .businessType(businessType)
            .throwable(throwable)
            .build());
      } catch (RuntimeException e) {
        log.error(e.toString(), e);
      }
    }
  }

  private void log(LogMessage logMessage) {
    StringMapMessage stringMapMessage = new StringMapMessage();
    addLogString(stringMapMessage, PARAM_REQUEST_HEADERS,
        toHeaderString(logMessage.requestHeaders()));
    addLogString(stringMapMessage, PARAM_RESPONSE_HEADERS,
        toHeaderString(logMessage.responseHeaders()));
    addLogString(stringMapMessage, PARAM_URL_FULL, logMessage.url().toString());
    addLogString(stringMapMessage, PARAM_URL_DOMAIN, logMessage.url().getHost());
    addLogString(stringMapMessage, PARAM_URL_EXTENSION,
        extractExtension(logMessage.url().getPath()));
    addLogString(stringMapMessage, PARAM_URL_PATH, logMessage.url().getPath());
    addLogString(stringMapMessage, PARAM_URL_PORT, Integer.toString(logMessage.url().getPort()));
    addLogString(stringMapMessage, PARAM_URL_SCHEME, logMessage.url().getProtocol());
    addLogString(stringMapMessage, PARAM_URL_QUERY, logMessage.url().getQuery());
    addLogString(stringMapMessage, PARAM_REQUEST_METHOD, logMessage.method());
    addLogString(stringMapMessage, PARAM_REQUEST_REFERER,
        getHeader(logMessage.requestHeaders(), HttpHeaders.REFERER));
    addLogString(stringMapMessage, PARAM_REQUEST_MIMETYPE, logMessage.requestMimeType());
    addLogString(stringMapMessage, PARAM_RESPONSE_MIMETYPE, logMessage.responseMimeType());
    addLogString(stringMapMessage, PARAM_REQUEST_BYTES, Integer.toString(logMessage.requestSize()));
    addLogString(stringMapMessage, PARAM_RESPONSE_BYTES,
        Integer.toString(logMessage.responseSize()));
    addLogString(stringMapMessage, PARAM_RESPONSE_STATUS,
        Integer.toString(logMessage.httpStatus()));
    addLogString(stringMapMessage, PARAM_DIRECTION, logMessage.direction());
    addLogString(stringMapMessage, PARAM_PROTOCOL, PROTOCOL_NAME);
    addLogString(stringMapMessage, PARAM_REQUEST_TIME,
        logMessage.requestTime().format(DATE_TIME_FORMATTER));
    addLogString(stringMapMessage, PARAM_RESPONSE_TIME,
        logMessage.responseTime().format(DATE_TIME_FORMATTER));
    addLogString(stringMapMessage, PARAM_DURATION,
        Long.toString(getDurationBetweenRequestAndResponseTime(logMessage).toNanos()));
    addLogString(stringMapMessage, PARAM_USER_AGENT,
        getHeader(logMessage.requestHeaders(), HttpHeaders.USER_AGENT));
    addLogString(stringMapMessage, HeaderInterceptor.LOGGER_TRACE_ID, logMessage.traceId());
    addLogString(stringMapMessage, HeaderInterceptor.LOGGER_REQTYPE_ID, logMessage.requestType());
    addLogString(stringMapMessage, PARAM_BUSINESS_TYPE, logMessage.businessType());
    addLogString(stringMapMessage, PARAM_REQUEST_BODY, cutToMaxLength(logMessage.requestBody()));
    addLogString(stringMapMessage, PARAM_RESPONSE_BODY, cutToMaxLength(logMessage.responseBody()));

    log.debug(MARKER, stringMapMessage, logMessage.throwable());
  }

  private Duration getDurationBetweenRequestAndResponseTime(LogMessage logMessage) {
    return Duration.between(logMessage.requestTime(), logMessage.responseTime());
  }

  private String getHeader(Map<String, Collection<String>> headers, String headerKey) {
    return headers.entrySet().stream()
        .filter(e -> e.getKey().equalsIgnoreCase(headerKey))
        .flatMap(e -> e.getValue().stream())
        .findAny()
        .orElse(null);
  }

  private void addLogString(StringMapMessage stringMapMessage, String key, String value) {
    if (value != null && !value.isBlank()) {
      stringMapMessage.with(key, value.trim());
    }
  }

  private static Map<String, Collection<String>> extractHeaders(Iterator<String> headerNames,
      Function<String, Iterator<String>> headerValuesSupplier) {
    Map<String, Collection<String>> requestHeaders = new HashMap<>();
    while (headerNames.hasNext()) {
      String name = headerNames.next();
      Collection<String> values = requestHeaders.computeIfAbsent(name, n -> new TreeSet<>());
      Iterator<String> headerValues = headerValuesSupplier.apply(name);
      while (headerValues.hasNext()) {
        values.add(headerValues.next());
      }
    }
    return requestHeaders;
  }

  private static Map<String, Collection<String>> extractHeaders(HttpHeaders headers) {
    Map<String, Collection<String>> result = new HashMap<>();
    for (Entry<String, List<String>> entry : headers.entrySet()) {
      result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }
    return result;
  }

  private static String toHeaderString(Map<String, Collection<String>> headerMap) {
    return headerMap.entrySet().stream()
        .flatMap(es -> es.getValue().stream().map(v -> new KeyValuePair(es.getKey(), v)))
        .map(kv -> kv.key() + "=" + kv.value())
        .collect(Collectors.joining("\n"));
  }

  private static String typeToString(String contentType) {
    try {
      return Optional.ofNullable(contentType)
          .map(MediaType::parseMediaType)
          .map(LoggingInterceptorRest::typeToString)
          .orElse(null);
    } catch (RuntimeException e) {
      log.info(e.toString(), e);
      return e.toString();
    }
  }

  private static String typeToString(MediaType mediaType) {
    try {
      return Optional.ofNullable(mediaType)
          .map(m -> m.getType() + "/" + m.getSubtype())
          .orElse(null);
    } catch (RuntimeException e) {
      log.info(e.toString(), e);
      return e.toString();
    }
  }

  private static String extractExtension(String fileName) {
    return Optional.ofNullable(fileName)
        .filter(name -> name.contains("."))
        .map(name -> name.substring(name.lastIndexOf('.') + 1))
        .orElse(null);
  }

  private static String cutToMaxLength(String string) {
    if (string != null && string.length() > MAX_LOG_SIZE) {
      return string.substring(0, MAX_LOG_SIZE);
    }
    return string;
  }

  /**
   * usually returns null, but can be overridden to implement more complex logic
   */
  public String determineBusinessType(URL requestUrl, String requestBody) {
    return null;
  }

  /**
   * usually returns UTF-8, but can be overridden to implement more complex logic
   */
  public Charset determineRequestEncoding(Map<String, Collection<String>> requestHeaders,
      byte[] fullRequest) {
    return StandardCharsets.UTF_8;
  }

  /**
   * usually returns UTF-8, but can be overridden to implement more complex logic
   */
  public Charset determineResponseEncoding(Map<String, Collection<String>> responseHeaders,
      byte[] fullResponse) {
    return StandardCharsets.UTF_8;
  }

  @Builder
  private record LogMessage(Map<String, Collection<String>> requestHeaders,
                            Map<String, Collection<String>> responseHeaders, URL url, String method,
                            String requestMimeType, String responseMimeType, String requestBody,
                            String responseBody,
                            int requestSize, int responseSize, int httpStatus, String direction,
                            ZonedDateTime requestTime, ZonedDateTime responseTime, String traceId,
                            String requestType,
                            String businessType, Throwable throwable) {

  }

  private record KeyValuePair(String key, String value) {

  }

  public enum FieldLogBehaviour {
    NEVER, ONLY_ON_ERROR, ALWAYS
  }

}
