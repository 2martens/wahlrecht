package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.Candidate;
import de.twomartens.wahlrecht.model.Constituency;
import de.twomartens.wahlrecht.model.ElectedResult;
import de.twomartens.wahlrecht.model.Nomination;
import de.twomartens.wahlrecht.model.VotingResult;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ConstituencyCalculationService {
  private final Deque<Double> electionNumberHistory = new ArrayDeque<>();

  public ElectedResult calculateConstituency(@NonNull Constituency constituency,
      @NonNull Collection<Nomination> nominations) {
    electionNumberHistory.clear();
    int totalVotes = nominations.stream()
        .map(Nomination::getVotingResult)
        .map(VotingResult::getTotalVotes)
        .reduce(0, Integer::sum);
    int numberOfSeats = constituency.numberOfSeats();
    double initialElectionNumber = totalVotes / (double) numberOfSeats;

    Map<Nomination, Long> assignedSeatsPerNomination = calculateAssignedSeatsPerNomination(
        nominations, numberOfSeats, initialElectionNumber);

    return ElectedResult.builder()
        .electedCandidates(findElectedCandidates(assignedSeatsPerNomination))
        .usedElectionNumbers(electionNumberHistory)
        .build();
  }

  @NonNull
  private Map<Nomination, Collection<Candidate>> findElectedCandidates(
      @NonNull Map<Nomination, Long> assignedSeatsPerNomination) {

    Map<Nomination, Collection<Candidate>> electedCandidates = new HashMap<>();
    assignedSeatsPerNomination.entrySet().stream()
        .filter(entry -> entry.getValue() > 0)
        .forEach(entry ->
            electedCandidates.put(entry.getKey(), findCandidates(entry.getKey(), entry.getValue())));

    return electedCandidates;
  }

  @NonNull
  private Collection<Candidate> findCandidates(@NonNull Nomination nomination, long numberOfSeats) {
    List<Integer> positions = nomination.getVotingResult().getVotesPerPosition().entrySet().stream()
        .sorted(Comparator.comparing(Entry<Integer, Integer>::getValue).reversed())
        .map(Entry::getKey)
        .toList();
    Collection<Candidate> elected = new ArrayList<>();
    for (int i = 0; i < numberOfSeats; i++) {
      Integer position = positions.get(i);
      elected.add(nomination.getCandidate(position));
    }

    return elected;
  }

  private Map<Nomination, Long> calculateAssignedSeatsPerNomination(
      @NonNull Collection<Nomination> nominations,
      int numberOfSeats, Double initialElectionNumber) {

    double electionNumber = initialElectionNumber;
    long assignedSeats;
    Map<Nomination, Long> assignedSeatsPerNomination;

    do {
      electionNumberHistory.add(electionNumber);
      Map<Nomination, Double> seatsPerNomination = new HashMap<>();
      for (Nomination nomination : nominations) {
        double seatNumber = calculateAssignedSeatNumber(electionNumber,
            nomination.getVotingResult().getTotalVotes());
        seatsPerNomination.put(nomination, seatNumber);
      }
      assignedSeatsPerNomination = seatsPerNomination.entrySet().stream()
          .map(entry -> Map.entry(entry.getKey(), Math.round(entry.getValue())))
          .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
      assignedSeats = assignedSeatsPerNomination.values().stream().reduce(0L, Long::sum);

      if (assignedSeats < numberOfSeats) {
        // election number was too big, decrease
        electionNumber = calculateLowerElectionNumber(initialElectionNumber, seatsPerNomination,
            assignedSeatsPerNomination);
      } else if (assignedSeats > numberOfSeats) {
        // election number was too small, increase
        electionNumber = calculateHigherElectionNumber(initialElectionNumber, seatsPerNomination,
            assignedSeatsPerNomination);
      }
    } while (assignedSeats != numberOfSeats);


    return assignedSeatsPerNomination;
  }

  private static double calculateHigherElectionNumber(Double initialElectionNumber,
      @NonNull Map<Nomination, Double> seatsPerNomination,
      Map<Nomination, Long> assignedSeatsPerNomination) {
    double electionNumber;
    electionNumber = seatsPerNomination.entrySet().stream()
        .map(entry -> Map.entry(entry.getKey(),
            entry.getValue() - assignedSeatsPerNomination.get(entry.getKey())))
        .min(Comparator.comparing(Entry<Nomination, Double>::getValue))
        .map(entry -> entry.getKey().getVotingResult().getTotalVotes()
            / (assignedSeatsPerNomination.get(entry.getKey()) - 0.5))
        .orElse(initialElectionNumber);
    return electionNumber;
  }

  private static double calculateLowerElectionNumber(Double initialElectionNumber,
      @NonNull Map<Nomination, Double> seatsPerNomination,
      Map<Nomination, Long> assignedSeatsPerNomination) {
    double electionNumber;
    electionNumber = seatsPerNomination.entrySet().stream()
        .map(entry -> Map.entry(entry.getKey(),
            entry.getValue() - assignedSeatsPerNomination.get(entry.getKey())))
        .max(Comparator.comparing(Entry<Nomination, Double>::getValue))
        .map(entry -> entry.getKey().getVotingResult().getTotalVotes()
            / (assignedSeatsPerNomination.get(entry.getKey()) + 0.5))
        .orElse(initialElectionNumber);
    return electionNumber;
  }



  private double calculateAssignedSeatNumber(double electionNumber, int totalVotesOfNomination) {
    return totalVotesOfNomination / electionNumber;
  }
}
