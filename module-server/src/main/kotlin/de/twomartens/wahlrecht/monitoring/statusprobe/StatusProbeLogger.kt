package de.twomartens.wahlrecht.monitoring.statusprobe;

import static de.twomartens.wahlrecht.monitoring.statusprobe.StatusProbeCriticality.K1;
import static de.twomartens.wahlrecht.monitoring.statusprobe.StatusProbeCriticality.K2;
import static de.twomartens.wahlrecht.monitoring.statusprobe.StatusProbeCriticality.K3;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.boot.actuate.health.Status;

public class StatusProbeLogger {

  private static final Marker MARKER = MarkerManager.getMarker("statusprobe");

  private static final String LABEL_CRITICALITY = "label.status.criticality";
  private static final String LABEL_STATUS = "label.status.status";
  private static final String LABEL_REASON = "label.status.reason";
  private static final String LABEL_MESSAGE = "label.status.description";
  private static final String LABEL_LAST_STATUS_CHANGE = "label.status.last_change";

  private final Clock clock;
  private final Logger commLog;
  private final Map<ProbeIdent, Status> statusProbeToStatus = new HashMap<>();

  public StatusProbeLogger(Clock clock) {
    this(clock, LogManager.getLogger("statusprobe"));
  }

  /**
   * only for testing purposes
   */
  StatusProbeLogger(Clock clock, Logger commLog) {
    this.clock = clock;
    this.commLog = commLog;
  }

  public void registerStatusProbe(String name, StatusProbeCriticality criticality) {
    statusProbeToStatus.put(new ProbeIdent(name, criticality), Status.UP);
    logStatusChange(name, "Startup", Status.UP, ZonedDateTime.now(clock), null);
  }

  public void logStatusChange(String name, String message, Status status, ZonedDateTime lastStatusChange,
      Throwable throwable) {
    ProbeIdent probeIdent = getProbeIdent(name);
    if (probeIdent == null) {
      probeIdent = new ProbeIdent(name, K1);
    }
    statusProbeToStatus.put(probeIdent, status);
    createLog(message, lastStatusChange, throwable);
  }

  private ProbeIdent getProbeIdent(String name) {
    return statusProbeToStatus.keySet().stream().filter(key -> key.name.equals(name)).findFirst().orElse(null);
  }

  private void createLog(String message, ZonedDateTime lastStatusChange, Throwable throwable) {
    Status overallStatus = getOverallStatus();
    StatusProbeCriticality criticality = getOverallCriticality();
    if (message == null) {
      message = "";
    }
    if (Status.UP.equals(overallStatus)) {
      commLog.info(MARKER, new StringMapMessage()
          .with(LABEL_CRITICALITY, criticality)
          .with(LABEL_STATUS, overallStatus)
          .with(LABEL_MESSAGE, message)
          .with(LABEL_LAST_STATUS_CHANGE, lastStatusChange));
    } else {
      commLog.error(MARKER, new StringMapMessage()
          .with(LABEL_CRITICALITY, criticality)
          .with(LABEL_STATUS, overallStatus)
          .with(LABEL_MESSAGE, message)
          .with(LABEL_REASON, getReason())
          .with(LABEL_LAST_STATUS_CHANGE, lastStatusChange), throwable);
    }
  }

  private StatusProbeCriticality getOverallCriticality() {
    List<StatusProbeCriticality> crits = statusProbeToStatus.keySet().stream().map(key -> key.criticality).toList();
    return crits.contains(K1) ? K1 : crits.contains(K2) ? K2 : K3;
  }

  private Status getOverallStatus() {
    if (statusProbeToStatus.containsValue(Status.DOWN)) {
      return Status.DOWN;
    }
    return Status.UP;
  }

  private String getReason() {

    List<ProbeIdent> probesDown = statusProbeToStatus.entrySet().stream()
        .filter(entry -> Status.DOWN.equals(entry.getValue()))
        .map(Map.Entry::getKey)
        .toList();

    String reasonK1 = getDownStatusProbes(probesDown, K1);
    String reasonK2 = getDownStatusProbes(probesDown, K2);
    String reasonK3 = getDownStatusProbes(probesDown, K3);

    return "%s%s%s".formatted(reasonK1, reasonK2, reasonK3).trim();
  }

  private String getDownStatusProbes(List<ProbeIdent> probesDown, StatusProbeCriticality criticality) {
    List<String> downProbeNames = probesDown.stream().filter(probe -> probe.criticality.equals(criticality))
        .map(probe -> probe.name).toList();
    if (downProbeNames.size() > 0) {
      return criticality + " failed: " + String.join(",", downProbeNames) + "\n";
    }
    return "";
  }

  public record ProbeIdent(String name, StatusProbeCriticality criticality) {

  }
}
