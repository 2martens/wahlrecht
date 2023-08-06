package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.model.internal.*
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.util.StopWatch
import kotlin.math.roundToInt

@Service
class CalculationService(
    private val nominationService: NominationService,
    private val electionService: ElectionService
) {
    private val electionNumberHistory: MutableList<Double> = mutableListOf()

    fun determineElectedCandidates(electionResult: ElectionResult): ElectedCandidates {
        log.info("Calculate election result for election {}", electionResult.electionName)
        val stopWatch = StopWatch()
        stopWatch.start("determineElectedCandidates")
        val election = electionService.getElectionInternal(electionResult.electionName)
        val seatResult = calculateOverallSeatDistribution(
            election,
            electionResult.overallResults
        )
        val constituencyResults: MutableMap<Int, ElectedResult> = HashMap()
        for (constituency in election.constituencies) {
            val electedResult = calculateConstituency(
                constituency,
                electionResult.constituencyResults[constituency.number]!!
            )
            constituencyResults[constituency.number] = electedResult
        }
        val electedCandidatesMap = constituencyResults.entries.asSequence()
            .flatMap { it.value.electedCandidates.entries.asSequence() }
            .groupingBy { it.key.nominationId.partyAbbreviation }
            .fold(listOf<ElectedCandidate>()) { accumulator, element -> accumulator + element.value }
        val remainingSeatsPerParty = seatResult.seatsPerResult.entries.asSequence()
            .map {
                Pair(
                    it.key,
                    it.value - (electedCandidatesMap[it.key.nominationId.partyAbbreviation] ?: emptyList()).size
                )
            }
            .toMap()
        val overallElectedCandidates = calculateElectedOverallCandidates(
            remainingSeatsPerParty,
            electedCandidatesMap
        )
        stopWatch.stop()
        log.debug("determineCandidates took {} seconds", stopWatch.totalTimeSeconds)
        return ElectedCandidates(
            overallResult = overallElectedCandidates,
            constituencyResults = constituencyResults,
            electionNumbersForSeatAllocation = seatResult.usedElectionNumbers,
            seatAllocation = seatResult.seatsPerNomination
                    .map { Pair(it.key.partyAbbreviation, it.value) }
                    .toMap()
        )
    }

    fun calculateConstituency(
        constituency: Constituency,
        votingResults: Collection<VotingResult>
    ): ElectedResult {
        electionNumberHistory.clear()
        log.info("Calculate constituency {}", constituency.name)
        val totalVotes = votingResults.asSequence()
            .map { it.totalVotes }
            .reduce { acc: Int, value: Int -> acc + value }
        val numberOfSeats = constituency.numberOfSeats
        val initialElectionNumber = totalVotes / numberOfSeats.toDouble()
        val assignedSeatsPerResult = calculateAssignedSeatsPerResult(
            votingResults, numberOfSeats, initialElectionNumber
        )
        return ElectedResult(
            electedCandidates = findElectedCandidates(assignedSeatsPerResult, emptyMap()),
            usedElectionNumbers = electionNumberHistory.toList()
        )
    }

    fun calculateOverallSeatDistribution(
        election: Election,
        votingResults: Collection<VotingResult>
    ): SeatResult {
        log.info("Calculate overall seat distribution for election {}", election.name)
        electionNumberHistory.clear()
        var totalVotes = votingResults.asSequence()
            .map { it.totalVotes }
            .reduce { acc: Int, value: Int -> acc + value }
        val validVotingResults: MutableList<VotingResult> = mutableListOf()
        var totalIgnoredVotes = 0
        for (votingResult in votingResults) {
            if (passesVotingThreshold(election, totalVotes, votingResult)) {
                log.info(
                    "Party passes voting threshold: {}",
                    votingResult.nominationId.partyAbbreviation
                )
                validVotingResults.add(votingResult)
            } else {
                log.info(
                    "Party fails voting threshold: {}",
                    votingResult.nominationId.partyAbbreviation
                )
                totalIgnoredVotes += votingResult.totalVotes
            }
        }
        totalVotes -= totalIgnoredVotes
        val numberOfSeats = election.totalNumberOfSeats
        val initialElectionNumber = totalVotes / numberOfSeats.toDouble()
        val assignedSeatsPerResult = calculateAssignedSeatsPerResult(
            validVotingResults, numberOfSeats, initialElectionNumber
        )
        return SeatResult(
            seatsPerResult = assignedSeatsPerResult,
            usedElectionNumbers = electionNumberHistory.toList()
        )
    }

    /**
     * Finds all elected candidates for given voting results
     *
     * @param seatsPerNomination Seats to allocate per voting result
     * @param electedCandidates Already elected candidates per party
     */
    fun calculateElectedOverallCandidates(
        seatsPerNomination: Map<VotingResult, Int>,
        electedCandidates: Map<String, Collection<ElectedCandidate>>
    ): ElectedResult {
        log.info("Calculate overall elected candidates")
        electionNumberHistory.clear()
        return ElectedResult(
            electedCandidates = findElectedCandidates(seatsPerNomination, electedCandidates),
            usedElectionNumbers = electionNumberHistory.toList()
        )
    }

    private fun passesVotingThreshold(
        election: Election, totalVotes: Int,
        votingResult: VotingResult
    ): Boolean {
        return (totalVotes * election.votingThreshold.multiplier
                <= votingResult.totalVotes)
    }

    /**
     * Finds all elected candidates for given voting results.
     *
     * @param assignedSeatsPerNomination Map of seats to allocate for a voting result
     * @param alreadyElectedCandidates Map of already elected candidates per party
     */
    private fun findElectedCandidates(
        assignedSeatsPerNomination: Map<VotingResult, Int>,
        alreadyElectedCandidates: Map<String, Collection<ElectedCandidate>>
    ): Map<VotingResult, Collection<ElectedCandidate>> {
        val electedCandidates: MutableMap<VotingResult, Collection<ElectedCandidate>> = HashMap()
        for ((key, value) in assignedSeatsPerNomination) {
            if (value > 0) {
                val candidates = findCandidates(
                    key,
                    value,
                    alreadyElectedCandidates[key.nominationId.partyAbbreviation] ?: emptyList()
                )
                electedCandidates[key] = candidates
            }
        }
        return electedCandidates
    }

    /**
     * Finds elected candidates for given voting result.
     *
     * @param votingResult Result to search candidates for
     * @param numberOfSeats Number of seats to allocate
     * @param alreadyElectedCandidates Candidates already elected previously
     */
    private fun findCandidates(
        votingResult: VotingResult,
        numberOfSeats: Int, alreadyElectedCandidates: Collection<ElectedCandidate>
    ): Collection<ElectedCandidate> {
        log.info(
            "Find elected candidates on nomination: {}",
            votingResult.nominationId.name
        )
        val individualVotesOrder = votingResult.votesPerPosition.entries.asSequence()
            .sortedByDescending { it.value }
            .map { it.key }
            .toList()
        val elected: MutableCollection<ElectedCandidate> = mutableListOf()
        var seatsByVoteOrder = numberOfSeats
        val electedCandidateMap: MutableMap<Candidate, ElectedCandidate> = alreadyElectedCandidates.asSequence()
            .map { Pair(it.candidate, it) }
            .toMap()
            .toMutableMap()
        val nominationId = votingResult.nominationId
        val nomination = nominationService.getNominationInternal(nominationId)
        if (nomination.supportsVotesOnNomination()) {
            val seatsByNomination = calculateSeatsByNominationOrder(votingResult, numberOfSeats)
            seatsByVoteOrder = numberOfSeats - seatsByNomination
            val candidates = nomination.getCandidates()
            var electedByNominationOrder = 0
            var i = 0
            while (i < candidates.size && electedByNominationOrder < seatsByNomination) {
                val candidate = candidates[i]
                var electedCandidate = electedCandidateMap[candidate]
                if (electedCandidate == null) {
                    electedCandidate = ElectedCandidate(candidate, Elected.OVERALL_NOMINATION_ORDER)
                    elected.add(electedCandidate)
                    electedCandidateMap[candidate] = electedCandidate
                    electedByNominationOrder++
                }
                i++
            }
        }
        var electedByVoteOrder = 0
        var i = 0
        while (i < individualVotesOrder.size && electedByVoteOrder < seatsByVoteOrder) {
            val position = individualVotesOrder[i]
            val candidate = nomination.getCandidate(position)
            var electedCandidate = electedCandidateMap[candidate]
            if (electedCandidate == null) {
                electedCandidate = ElectedCandidate(
                    candidate,
                    if (nomination.supportsVotesOnNomination()) Elected.OVERALL_VOTE_ORDER else Elected.CONSTITUENCY
                )
                elected.add(electedCandidate)
                electedByVoteOrder++
            }
            i++
        }
        return elected
    }

    private fun calculateSeatsByNominationOrder(
        votingResult: VotingResult,
        numberOfSeats: Int
    ): Int {
        val votesOnNomination = votingResult.votesOnNomination
        val totalVotes = votingResult.totalVotesWithoutHealing
        return Math.round(numberOfSeats * votesOnNomination / totalVotes.toDouble()).toInt()
    }

    private fun calculateAssignedSeatsPerResult(
        votingResults: Collection<VotingResult>,
        numberOfSeats: Int, initialElectionNumber: Double
    ): Map<VotingResult, Int> {
        var electionNumber = initialElectionNumber
        var assignedSeats: Long
        var assignedSeatsPerVotingResult: Map<VotingResult, Int>
        log.debug("Calculate assigned seats with initial election number {}", electionNumber)
        do {
            electionNumberHistory.add(electionNumber)
            val seatsPerVotingResult: MutableMap<VotingResult, Double> = mutableMapOf()
            for (votingResult in votingResults) {
                val seatNumber = calculateAssignedSeatNumber(
                    electionNumber,
                    votingResult.totalVotes
                )
                seatsPerVotingResult[votingResult] = seatNumber
            }
            assignedSeatsPerVotingResult = seatsPerVotingResult.entries.asSequence()
                .map { Pair(it.key, it.value.roundToInt()) }
                .toMap()
            assignedSeats = assignedSeatsPerVotingResult.values
                .reduce { acc: Int, newValue: Int -> acc + newValue }.toLong()
            if (assignedSeats < numberOfSeats) {
                // election number was too big, decrease
                electionNumber = calculateLowerElectionNumber(
                    initialElectionNumber, seatsPerVotingResult,
                    assignedSeatsPerVotingResult
                )
                log.debug("Calculated lower election number {}", electionNumber)
            } else if (assignedSeats > numberOfSeats) {
                // election number was too small, increase
                electionNumber = calculateHigherElectionNumber(
                    initialElectionNumber, seatsPerVotingResult,
                    assignedSeatsPerVotingResult
                )
                log.debug("Calculated higher election number {}", electionNumber)
            }
        } while (assignedSeats != numberOfSeats.toLong())
        return assignedSeatsPerVotingResult
    }

    private fun calculateHigherElectionNumber(
        initialElectionNumber: Double,
        seatsPerVotingResult: Map<VotingResult, Double>,
        assignedSeatsPerVotingResult: Map<VotingResult, Int>
    ): Double {
        return if (seatsPerVotingResult.isEmpty()) {
            initialElectionNumber
        } else {
            seatsPerVotingResult.keys.asSequence()
                .map { it.totalVotes / (assignedSeatsPerVotingResult[it]!! - 0.5) }
                .min()
        }
    }

    private fun calculateLowerElectionNumber(
        initialElectionNumber: Double,
        seatsPerVotingResult: Map<VotingResult, Double>,
        assignedSeatsPerVotingResult: Map<VotingResult, Int>
    ): Double {
        return if (seatsPerVotingResult.isEmpty()) {
            initialElectionNumber
        } else {
            seatsPerVotingResult.keys.asSequence()
                .map { it.totalVotes / (assignedSeatsPerVotingResult[it]!! + 0.5) }
                .max()
        }
    }


    private fun calculateAssignedSeatNumber(electionNumber: Double, totalVotesOfNomination: Int): Double {
        return totalVotesOfNomination / electionNumber
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}