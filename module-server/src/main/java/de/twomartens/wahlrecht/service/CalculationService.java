package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.Candidate;
import de.twomartens.wahlrecht.model.Constituency;
import de.twomartens.wahlrecht.model.Elected;
import de.twomartens.wahlrecht.model.ElectedCandidate;
import de.twomartens.wahlrecht.model.ElectedResult;
import de.twomartens.wahlrecht.model.Election;
import de.twomartens.wahlrecht.model.Nomination;
import de.twomartens.wahlrecht.model.SeatResult;
import de.twomartens.wahlrecht.model.VotingResult;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class CalculationService {
  private final Deque<Double> electionNumberHistory = new ArrayDeque<>();

  public ElectedResult calculateConstituency(@NonNull Constituency constituency,
      @NonNull Collection<VotingResult> votingResults) {
    electionNumberHistory.clear();
    int totalVotes = votingResults.stream()
        .map(VotingResult::getTotalVotes)
        .reduce(0, Integer::sum);
    int numberOfSeats = constituency.numberOfSeats();
    double initialElectionNumber = totalVotes / (double) numberOfSeats;

    Map<VotingResult, Integer> assignedSeatsPerResult = calculateAssignedSeatsPerResult(
        votingResults, numberOfSeats, initialElectionNumber);

    return ElectedResult.builder()
        .electedCandidates(findElectedCandidates(assignedSeatsPerResult, Collections.emptyMap()))
        .usedElectionNumbers(electionNumberHistory)
        .build();
  }

  public SeatResult calculateOverallSeatDistribution(@NonNull Election election,
      @NonNull Collection<VotingResult> votingResults) {
    electionNumberHistory.clear();
    int totalVotes = votingResults.stream()
        .map(VotingResult::getTotalVotes)
        .reduce(0, Integer::sum);

    List<VotingResult> validVotingResults = new ArrayList<>();
    int totalIgnoredVotes = 0;
    for (VotingResult votingResult : votingResults) {
      if (passesVotingThreshold(election, totalVotes, votingResult)) {
        validVotingResults.add(votingResult);
      } else {
        totalIgnoredVotes += votingResult.getTotalVotes();
      }
    }
    totalVotes -= totalIgnoredVotes;

    int numberOfSeats = election.totalNumberOfSeats();
    double initialElectionNumber = totalVotes / (double) numberOfSeats;

    Map<VotingResult, Integer> assignedSeatsPerResult = calculateAssignedSeatsPerResult(
        validVotingResults, numberOfSeats, initialElectionNumber);

    return SeatResult.builder()
        .seatsPerResult(assignedSeatsPerResult)
        .usedElectionNumbers(electionNumberHistory)
        .build();
  }

  public ElectedResult calculateElectedOverallCandidates(Map<VotingResult, Integer> seatsPerNomination,
      Map<VotingResult, Collection<ElectedCandidate>> electedCandidates) {
    electionNumberHistory.clear();

    return ElectedResult.builder()
        .electedCandidates(findElectedCandidates(seatsPerNomination, electedCandidates))
        .usedElectionNumbers(electionNumberHistory)
        .build();
  }

  private static boolean passesVotingThreshold(@NonNull Election election, int totalVotes,
      @NonNull VotingResult votingResult) {
    return totalVotes * election.votingThreshold().getMultiplier()
        <= votingResult.getTotalVotes();
  }

  @NonNull
  private Map<VotingResult, Collection<ElectedCandidate>> findElectedCandidates(
      @NonNull Map<VotingResult, Integer> assignedSeatsPerNomination,
      Map<VotingResult, Collection<ElectedCandidate>> alreadyElectedCandidates) {

    Map<VotingResult, Collection<ElectedCandidate>> electedCandidates = new HashMap<>();
    for (Entry<VotingResult, Integer> entry : assignedSeatsPerNomination.entrySet()) {
      if (entry.getValue() > 0) {
        Collection<ElectedCandidate> candidates = findCandidates(
            entry.getKey(),
            entry.getValue(),
            alreadyElectedCandidates.getOrDefault(entry.getKey(), Collections.emptyList())
        );
        electedCandidates.put(entry.getKey(), candidates);
      }
    }

    return electedCandidates;
  }

  @NonNull
  private Collection<ElectedCandidate> findCandidates(@NonNull VotingResult votingResult,
      int numberOfSeats, Collection<ElectedCandidate> alreadyElectedCandidates) {
    List<Integer> individualVotesOrder = votingResult.getVotesPerPosition()
        .entrySet().stream()
        .sorted(Comparator.comparing(Entry<Integer, Integer>::getValue).reversed())
        .map(Entry::getKey)
        .toList();
    Collection<ElectedCandidate> elected = new ArrayList<>();
    int seatsByVoteOrder = numberOfSeats;

    Map<Candidate, ElectedCandidate> electedCandidateMap = alreadyElectedCandidates.stream()
        .collect(Collectors.toMap(ElectedCandidate::candidate, Function.identity()));

    Nomination nomination = votingResult.getNomination();
    if (nomination.supportsVotesOnNomination()) {
      int seatsByNomination = calculateSeatsByNominationOrder(votingResult, numberOfSeats);
      seatsByVoteOrder = numberOfSeats - seatsByNomination;
      List<Candidate> candidates = nomination.getCandidates();
      int electedByNominationOrder = 0;
      for (int i = 0; i < candidates.size() && electedByNominationOrder < seatsByNomination; i++) {
        Candidate candidate = candidates.get(i);
        ElectedCandidate electedCandidate = electedCandidateMap.get(candidate);
        if (electedCandidate == null) {
          electedCandidate = new ElectedCandidate(candidate, Elected.OVERALL_NOMINATION_ORDER);
          elected.add(electedCandidate);
          electedCandidateMap.put(candidate, electedCandidate);
          electedByNominationOrder++;
        }
      }
    }

    int electedByVoteOrder = 0;
    for (int i = 0; i < individualVotesOrder.size() && electedByVoteOrder < seatsByVoteOrder; i++) {
      Integer position = individualVotesOrder.get(i);
      Candidate candidate = nomination.getCandidate(position);
      ElectedCandidate electedCandidate = electedCandidateMap.get(candidate);
      if (electedCandidate == null) {
        electedCandidate = new ElectedCandidate(candidate, nomination.supportsVotesOnNomination()
            ? Elected.OVERALL_VOTE_ORDER
            : Elected.CONSTITUENCY);
        elected.add(electedCandidate);
        electedByVoteOrder++;
      }
    }

    return elected;
  }

  private static int calculateSeatsByNominationOrder(@NonNull VotingResult votingResult, int numberOfSeats) {
    int votesOnNomination = votingResult.getVotesOnNomination();
    int totalVotes = votingResult.getTotalVotesWithoutHealing();
    return (int) Math.round((numberOfSeats * votesOnNomination) / (double) totalVotes);
  }

  private Map<VotingResult, Integer> calculateAssignedSeatsPerResult(
      @NonNull Collection<VotingResult> votingResults,
      int numberOfSeats, Double initialElectionNumber) {

    double electionNumber = initialElectionNumber;
    long assignedSeats;
    Map<VotingResult, Integer> assignedSeatsPerVotingResult;

    do {
      electionNumberHistory.add(electionNumber);
      Map<VotingResult, Double> seatsPerVotingResult = new HashMap<>();
      for (VotingResult votingResult : votingResults) {
        double seatNumber = calculateAssignedSeatNumber(electionNumber,
            votingResult.getTotalVotes());
        seatsPerVotingResult.put(votingResult, seatNumber);
      }
      assignedSeatsPerVotingResult = seatsPerVotingResult.entrySet().stream()
          .map(entry -> Map.entry(entry.getKey(), (int) Math.round(entry.getValue())))
          .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
      assignedSeats = assignedSeatsPerVotingResult.values().stream().reduce(0, Integer::sum);

      if (assignedSeats < numberOfSeats) {
        // election number was too big, decrease
        electionNumber = calculateLowerElectionNumber(initialElectionNumber, seatsPerVotingResult,
            assignedSeatsPerVotingResult);
      } else if (assignedSeats > numberOfSeats) {
        // election number was too small, increase
        electionNumber = calculateHigherElectionNumber(initialElectionNumber, seatsPerVotingResult,
            assignedSeatsPerVotingResult);
      }
    } while (assignedSeats != numberOfSeats);


    return assignedSeatsPerVotingResult;
  }

  private static double calculateHigherElectionNumber(Double initialElectionNumber,
      @NonNull Map<VotingResult, Double> seatsPerVotingResult,
      Map<VotingResult, Integer> assignedSeatsPerVotingResult) {
    double electionNumber;
    electionNumber = seatsPerVotingResult.entrySet().stream()
        .map(entry -> Map.entry(entry.getKey(),
            entry.getValue() - assignedSeatsPerVotingResult.get(entry.getKey())))
        .min(Comparator.comparing(Entry<VotingResult, Double>::getValue))
        .map(entry -> entry.getKey().getTotalVotes()
            / (assignedSeatsPerVotingResult.get(entry.getKey()) - 0.5))
        .orElse(initialElectionNumber);
    return electionNumber;
  }

  private static double calculateLowerElectionNumber(Double initialElectionNumber,
      @NonNull Map<VotingResult, Double> seatsPerVotingResult,
      Map<VotingResult, Integer> assignedSeatsPerVotingResult) {
    double electionNumber;
    electionNumber = seatsPerVotingResult.entrySet().stream()
        .map(entry -> Map.entry(entry.getKey(),
            entry.getValue() - assignedSeatsPerVotingResult.get(entry.getKey())))
        .max(Comparator.comparing(Entry<VotingResult, Double>::getValue))
        .map(entry -> entry.getKey().getTotalVotes()
            / (assignedSeatsPerVotingResult.get(entry.getKey()) + 0.5))
        .orElse(initialElectionNumber);
    return electionNumber;
  }



  private double calculateAssignedSeatNumber(double electionNumber, int totalVotesOfNomination) {
    return totalVotesOfNomination / electionNumber;
  }
}
