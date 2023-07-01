package de.twomartens.template.monitoring.actuator;

import java.io.Closeable;

@FunctionalInterface
public interface Preparable {

  Closeable prepare();

}
