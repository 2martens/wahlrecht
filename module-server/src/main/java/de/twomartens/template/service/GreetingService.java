package de.twomartens.template.service;

import de.twomartens.template.model.db.Greeting;
import de.twomartens.template.property.ServiceProperties;
import de.twomartens.template.repository.GreetingRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GreetingService {

  private final MeterRegistry meterRegistry;
  private final ServiceProperties serviceProperties;
  private final Counter counter;

  private final GreetingRepository greetingRepository;

  public GreetingService(MeterRegistry meterRegistry, ServiceProperties serviceProperties,
      GreetingRepository greetingRepository) {
    this.meterRegistry = meterRegistry;
    this.serviceProperties = serviceProperties;
    this.greetingRepository = greetingRepository;
    counter = meterRegistry.counter("template.callCounter");
  }

  public Greeting createGreeting(String name) {
    log.info("Create greeting for '{}'", name);
    counter.increment();
    meterRegistry.gauge("template.nameLength", name.length());
    String greeting = serviceProperties.getGreeting();
    return Greeting.builder().message(String.format(greeting, name)).build();
  }

  public void postGreeting(Greeting greeting) {
    greetingRepository.save(greeting);
  }
}
