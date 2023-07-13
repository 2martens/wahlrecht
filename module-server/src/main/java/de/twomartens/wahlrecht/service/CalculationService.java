package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.internal.Candidate;
import de.twomartens.wahlrecht.model.internal.Constituency;
import de.twomartens.wahlrecht.model.internal.Elected;
import de.twomartens.wahlrecht.model.internal.ElectedCandidate;
import de.twomartens.wahlrecht.model.internal.ElectedCandidates;
import de.twomartens.wahlrecht.model.internal.ElectedResult;
import de.twomartens.wahlrecht.model.internal.Election;
import de.twomartens.wahlrecht.model.internal.ElectionResult;
import de.twomartens.wahlrecht.model.internal.Nomination;
import de.twomartens.wahlrecht.model.internal.NominationId;
import de.twomartens.wahlrecht.model.internal.SeatResult;
import de.twomartens.wahlrecht.model.internal.VotingResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@RequiredArgsConstructor
@Service
@Slf4j
public class CalculationService {

  private final NominationService nominationService;
  private final ElectionService electionService;
  private final LinkedList<Double> electionNumberHistory = new LinkedList<>();
  private StopWatch stopWatch;

  public ElectedCandidates determineElectedCandidates(@NonNull ElectionResult electionResult) {
    log.info("Calculate election result for election {}", electionResult.electionName());
    stopWatch = new StopWatch();
    stopWatch.start("determineElectedCandidates");
    Election election = electionService.getElectionInternal(electionResult.electionName());
    SeatResult seatResult = calculateOverallSeatDistribution(election,
        electionResult.overallResults());
    Map<Integer, ElectedResult> constituencyResults = new HashMap<>();

    for (Constituency constituency : election.constituencies()) {
      ElectedResult electedResult = calculateConstituency(constituency,
          electionResult.constituencyResults().get(constituency.number()));
      constituencyResults.put(constituency.number(), electedResult);
    }
    Map<String, Collection<ElectedCandidate>> electedCandidatesMap = constituencyResults
        .entrySet().stream()
        .flatMap(entry -> entry.getValue().electedCandidates().entrySet().stream())
        .collect(Collectors.groupingBy(entry -> entry.getKey().getNominationId().partyAbbreviation(),
            Collectors.flatMapping(entry -> entry.getValue().stream(),
                Collectors.toCollection(ArrayList::new))));

    Map<VotingResult, Integer> remainingSeatsPerParty = seatResult.seatsPerResult()
        .entrySet().stream()
        .map(entry -> Map.entry(entry.getKey(),
            entry.getValue() - electedCandidatesMap.getOrDefault(
                entry.getKey().getNominationId().partyAbbreviation(),
                Collections.emptyList()).size()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

    ElectedResult overallElectedCandidates = calculateElectedOverallCandidates(
        remainingSeatsPerParty,
        electedCandidatesMap
    );

    stopWatch.stop();
    log.debug("determineCandidates took {} seconds", stopWatch.getTotalTimeSeconds());

    return ElectedCandidates.builder()
        .overallResult(overallElectedCandidates)
        .constituencyResults(constituencyResults)
        .electionNumbersForSeatAllocation(seatResult.usedElectionNumbers())
        .build();
  }

  public ElectedResult calculateConstituency(@NonNull Constituency constituency,
      @NonNull Collection<VotingResult> votingResults) {
    electionNumberHistory.clear();
    log.info("Calculate constituency {}", constituency.name());
    int totalVotes = votingResults.stream()
        .map(VotingResult::getTotalVotes)
        .reduce(0, Integer::sum);
    int numberOfSeats = constituency.numberOfSeats();
    double initialElectionNumber = totalVotes / (double) numberOfSeats;

    Map<VotingResult, Integer> assignedSeatsPerResult = calculateAssignedSeatsPerResult(
        votingResults, numberOfSeats, initialElectionNumber);

    return ElectedResult.builder()
        .electedCandidates(findElectedCandidates(assignedSeatsPerResult, Collections.emptyMap()))
        .usedElectionNumbers(new LinkedList<>(electionNumberHistory))
        .build();
  }

  public SeatResult calculateOverallSeatDistribution(@NonNull Election election,
      @NonNull Collection<VotingResult> votingResults) {
    log.info("Calculate overall seat distribution for election {}", election.name());
    electionNumberHistory.clear();
    int totalVotes = votingResults.stream()
        .map(VotingResult::getTotalVotes)
        .reduce(0, Integer::sum);

    List<VotingResult> validVotingResults = new ArrayList<>();
    int totalIgnoredVotes = 0;
    for (VotingResult votingResult : votingResults) {
      if (passesVotingThreshold(election, totalVotes, votingResult)) {
        log.info("Party passes voting threshold: {}",
            votingResult.getNominationId().partyAbbreviation());
        validVotingResults.add(votingResult);
      } else {
        log.info("Party fails voting threshold: {}",
            votingResult.getNominationId().partyAbbreviation());
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
        .usedElectionNumbers(new LinkedList<>(electionNumberHistory))
        .build();
  }

  /**
   * Finds all elected candidates for given voting results
   *
   * @param seatsPerNomination Seats to allocate per voting result
   * @param electedCandidates Already elected candidates per party
   */
  public ElectedResult calculateElectedOverallCandidates(
      Map<VotingResult, Integer> seatsPerNomination,
      Map<String, Collection<ElectedCandidate>> electedCandidates) {
    log.info("Calculate overall elected candidates");
    electionNumberHistory.clear();

    return ElectedResult.builder()
        .electedCandidates(findElectedCandidates(seatsPerNomination, electedCandidates))
        .usedElectionNumbers(new LinkedList<>(electionNumberHistory))
        .build();
  }

  private static boolean passesVotingThreshold(@NonNull Election election, int totalVotes,
      @NonNull VotingResult votingResult) {
    return totalVotes * election.votingThreshold().getMultiplier()
        <= votingResult.getTotalVotes();
  }

  /**
   * Finds all elected candidates for given voting results.
   *
   * @param assignedSeatsPerNomination Map of seats to allocate for a voting result
   * @param alreadyElectedCandidates Map of already elected candidates per party
   */
  @NonNull
  private Map<VotingResult, Collection<ElectedCandidate>> findElectedCandidates(
      @NonNull Map<VotingResult, Integer> assignedSeatsPerNomination,
      Map<String, Collection<ElectedCandidate>> alreadyElectedCandidates) {

    Map<VotingResult, Collection<ElectedCandidate>> electedCandidates = new HashMap<>();
    for (Entry<VotingResult, Integer> entry : assignedSeatsPerNomination.entrySet()) {
      if (entry.getValue() > 0) {
        Collection<ElectedCandidate> candidates = findCandidates(
            entry.getKey(),
            entry.getValue(),
            alreadyElectedCandidates.getOrDefault(
                entry.getKey().getNominationId().partyAbbreviation(),
                Collections.emptyList())
        );
        electedCandidates.put(entry.getKey(), candidates);
      }
    }

    return electedCandidates;
  }

  /**
   * Finds elected candidates for given voting result.
   *
   * @param votingResult Result to search candidates for
   * @param numberOfSeats Number of seats to allocate
   * @param alreadyElectedCandidates Candidates already elected previously
   */
  @NonNull
  private Collection<ElectedCandidate> findCandidates(@NonNull VotingResult votingResult,
      int numberOfSeats, @NonNull Collection<ElectedCandidate> alreadyElectedCandidates) {
    log.info("Find elected candidates on nomination: {}",
        votingResult.getNominationId().name());
    List<Integer> individualVotesOrder = votingResult.getVotesPerPosition()
        .entrySet().stream()
        .sorted(Comparator.comparing(Entry<Integer, Integer>::getValue).reversed())
        .map(Entry::getKey)
        .toList();
    Collection<ElectedCandidate> elected = new ArrayList<>();
    int seatsByVoteOrder = numberOfSeats;

    Map<Candidate, ElectedCandidate> electedCandidateMap = alreadyElectedCandidates.stream()
        .collect(Collectors.toMap(ElectedCandidate::candidate, Function.identity()));

    NominationId nominationId = votingResult.getNominationId();
    Nomination nomination = nominationService.getNominationInternal(nominationId);
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

  private static int calculateSeatsByNominationOrder(@NonNull VotingResult votingResult,
      int numberOfSeats) {
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
    log.debug("Calculate assigned seats with initial election number {}", electionNumber);

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
        log.debug("Calculated lower election number {}", electionNumber);
      } else if (assignedSeats > numberOfSeats) {
        // election number was too small, increase
        electionNumber = calculateHigherElectionNumber(initialElectionNumber, seatsPerVotingResult,
            assignedSeatsPerVotingResult);
        log.debug("Calculated higher election number {}", electionNumber);
      }
    } while (assignedSeats != numberOfSeats);

    return assignedSeatsPerVotingResult;
  }

  private static double calculateHigherElectionNumber(Double initialElectionNumber,
      @NonNull Map<VotingResult, Double> seatsPerVotingResult,
      Map<VotingResult, Integer> assignedSeatsPerVotingResult) {
    double electionNumber;
    electionNumber = seatsPerVotingResult.keySet().stream()
        .map(votingResult -> votingResult.getTotalVotes()
            / (assignedSeatsPerVotingResult.get(votingResult) - 0.5))
        .min(Comparator.comparing(Double::doubleValue))
        .orElse(initialElectionNumber);
    return electionNumber;
  }

  private static double calculateLowerElectionNumber(Double initialElectionNumber,
      @NonNull Map<VotingResult, Double> seatsPerVotingResult,
      Map<VotingResult, Integer> assignedSeatsPerVotingResult) {
    double electionNumber;
    electionNumber = seatsPerVotingResult.keySet().stream()
        .map(votingResult -> votingResult.getTotalVotes()
            / (assignedSeatsPerVotingResult.get(votingResult) + 0.5))
        .max(Comparator.comparing(Double::doubleValue))
        .orElse(initialElectionNumber);
    return electionNumber;
  }


  private double calculateAssignedSeatNumber(double electionNumber, int totalVotesOfNomination) {
    return totalVotesOfNomination / electionNumber;
  }
}
