package de.twomartens.wahlrecht.monitoring.actuator;

import java.io.Closeable;

@FunctionalInterface
public interface Preparable {

  Closeable prepare();

}
