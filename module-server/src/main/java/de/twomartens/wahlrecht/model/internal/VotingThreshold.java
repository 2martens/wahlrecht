package de.twomartens.wahlrecht.model.internal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum VotingThreshold {
  NONE(0.00),
  THREE(0.03),
  FIVE(0.05);

  private final double multiplier;
}
