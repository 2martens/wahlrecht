package de.twomartens.template.service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * automatically reloads application*.yaml. reload can also be triggered manually by doing a post
 * request on http://localhost:12001/actuator/refresh
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class PropertyReloadService {

  public static final int REFRESH_SECONDS = 60;
  public static final String PARAM_MESSAGE = "message";
  public static final String PARAM_PROPERTIES = "labels.properties";

  private final ContextRefresher contextRefresher;

  @Scheduled(fixedDelay = REFRESH_SECONDS, initialDelay = REFRESH_SECONDS, timeUnit = TimeUnit.SECONDS)
  public void refresh() {
    Set<String> properties = contextRefresher.refresh();
    if (!properties.isEmpty()) {
      log.info(new StringMapMessage()
          .with(PARAM_MESSAGE, "properties changed")
          .with(PARAM_PROPERTIES, String.join("\n", properties)));
    }
  }

}
