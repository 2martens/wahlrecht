package de.twomartens.template;

import de.twomartens.template.model.Name;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  public static void main(String[] args) {
    Name name = new Name("World");
    log.info("Hello %s!".formatted(name));
  }
}
