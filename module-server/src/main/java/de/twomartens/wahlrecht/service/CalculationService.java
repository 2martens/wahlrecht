package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.Candidate;
import de.twomartens.wahlrecht.model.Constituency;
import de.twomartens.wahlrecht.model.Elected;
import de.twomartens.wahlrecht.model.ElectedResult;
import de.twomartens.wahlrecht.model.Election;
import de.twomartens.wahlrecht.model.Nomination;
import de.twomartens.wahlrecht.model.SeatResult;
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
public class CalculationService {
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

    Map<Nomination, Integer> assignedSeatsPerNomination = calculateAssignedSeatsPerNomination(
        nominations, numberOfSeats, initialElectionNumber);

    return ElectedResult.builder()
        .electedCandidates(findElectedCandidates(assignedSeatsPerNomination))
        .usedElectionNumbers(electionNumberHistory)
        .build();
  }

  public SeatResult calculateOverallSeatDistribution(@NonNull Election election,
      @NonNull Collection<Nomination> nominations) {
    electionNumberHistory.clear();
    int totalVotes = nominations.stream()
        .map(Nomination::getVotingResult)
        .map(VotingResult::getTotalVotes)
        .reduce(0, Integer::sum);

    List<Nomination> validNominations = new ArrayList<>();
    int totalIgnoredVotes = 0;
    for (Nomination nomination : nominations) {
      if (passesVotingThreshold(election, totalVotes, nomination)) {
        validNominations.add(nomination);
      } else {
        totalIgnoredVotes += nomination.getVotingResult().getTotalVotes();
      }
    }
    totalVotes -= totalIgnoredVotes;

    int numberOfSeats = election.totalNumberOfSeats();
    double initialElectionNumber = totalVotes / (double) numberOfSeats;

    Map<Nomination, Integer> assignedSeatsPerNomination = calculateAssignedSeatsPerNomination(
        validNominations, numberOfSeats, initialElectionNumber);

    return SeatResult.builder()
        .seatsPerNomination(assignedSeatsPerNomination)
        .usedElectionNumbers(electionNumberHistory)
        .build();
  }

  public ElectedResult calculateElectedOverallCandidates(Map<Nomination, Integer> seatsPerNomination) {
    electionNumberHistory.clear();

    return ElectedResult.builder()
        .electedCandidates(findElectedCandidates(seatsPerNomination))
        .usedElectionNumbers(electionNumberHistory)
        .build();
  }

  private static boolean passesVotingThreshold(Election election, int totalVotes, Nomination nomination) {
    return totalVotes * election.votingThreshold().getMultiplier()
        <= nomination.getVotingResult().getTotalVotes();
  }

  @NonNull
  private Map<Nomination, Collection<Candidate>> findElectedCandidates(
      @NonNull Map<Nomination, Integer> assignedSeatsPerNomination) {

    Map<Nomination, Collection<Candidate>> electedCandidates = new HashMap<>();
    assignedSeatsPerNomination.entrySet().stream()
        .filter(entry -> entry.getValue() > 0)
        .forEach(entry ->
            electedCandidates.put(entry.getKey(), findCandidates(entry.getKey(), entry.getValue())));

    return electedCandidates;
  }

  @NonNull
  private Collection<Candidate> findCandidates(@NonNull Nomination nomination, int numberOfSeats) {
    List<Integer> individualVotesOrder = nomination.getVotingResult().getVotesPerPosition()
        .entrySet().stream()
        .sorted(Comparator.comparing(Entry<Integer, Integer>::getValue).reversed())
        .map(Entry::getKey)
        .toList();
    Collection<Candidate> elected = new ArrayList<>();
    int seatsByVoteOrder = numberOfSeats;

    if (nomination.supportsVotesOnNomination()) {
      int seatsByNomination = calculateSeatsByNominationOrder(nomination, numberOfSeats);
      seatsByVoteOrder = numberOfSeats - seatsByNomination;
      List<Candidate> candidates = nomination.getCandidates();
      int electedByNominationOrder = 0;
      for (int i = 0; i < candidates.size() && electedByNominationOrder < seatsByNomination; i++) {
        Candidate candidate = candidates.get(i);
        if (candidate.getElected() == Elected.NOT_ELECTED) {
          candidate.setElected(Elected.OVERALL_NOMINATION_ORDER);
          elected.add(candidate);
          electedByNominationOrder++;
        }
      }
    }

    int electedByVoteOrder = 0;
    for (int i = 0; i < individualVotesOrder.size() && electedByVoteOrder < seatsByVoteOrder; i++) {
      Integer position = individualVotesOrder.get(i);
      Candidate candidate = nomination.getCandidate(position);
      if (candidate.getElected() == Elected.NOT_ELECTED) {
        candidate.setElected(nomination.supportsVotesOnNomination()
            ? Elected.OVERALL_VOTE_ORDER
            : Elected.CONSTITUENCY);
        elected.add(candidate);
        electedByVoteOrder++;
      }
    }

    return elected;
  }

  private static int calculateSeatsByNominationOrder(@NonNull Nomination nomination, int numberOfSeats) {
    int votesOnNomination = nomination.getVotingResult().getVotesOnNomination();
    int totalVotes = nomination.getVotingResult().getTotalVotesWithoutHealing();
    return (int) Math.round((numberOfSeats * votesOnNomination) / (double) totalVotes);
  }

  private Map<Nomination, Integer> calculateAssignedSeatsPerNomination(
      @NonNull Collection<Nomination> nominations,
      int numberOfSeats, Double initialElectionNumber) {

    double electionNumber = initialElectionNumber;
    long assignedSeats;
    Map<Nomination, Integer> assignedSeatsPerNomination;

    do {
      electionNumberHistory.add(electionNumber);
      Map<Nomination, Double> seatsPerNomination = new HashMap<>();
      for (Nomination nomination : nominations) {
        double seatNumber = calculateAssignedSeatNumber(electionNumber,
            nomination.getVotingResult().getTotalVotes());
        seatsPerNomination.put(nomination, seatNumber);
      }
      assignedSeatsPerNomination = seatsPerNomination.entrySet().stream()
          .map(entry -> Map.entry(entry.getKey(), (int) Math.round(entry.getValue())))
          .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
      assignedSeats = assignedSeatsPerNomination.values().stream().reduce(0, Integer::sum);

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
      Map<Nomination, Integer> assignedSeatsPerNomination) {
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
      Map<Nomination, Integer> assignedSeatsPerNomination) {
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
