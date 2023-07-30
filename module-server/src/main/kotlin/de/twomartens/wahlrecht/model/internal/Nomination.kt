package de.twomartens.wahlrecht.model.internal

data class Nomination(val id: NominationId, val supportVotesOnNomination: Boolean) {
    private val candidates: MutableList<Candidate> = mutableListOf()
    private val candidateToPosition: MutableMap<Candidate, Int> = mutableMapOf()
    private val positionToCandidate: MutableMap<Int, Candidate> = mutableMapOf()

    private fun addCandidate(candidate: Candidate) {
        candidates.add(candidate)
        candidateToPosition[candidate] = candidates.size
        positionToCandidate[candidates.size] = candidate
    }

    fun addCandidates(vararg candidates: Candidate) {
        candidates.forEach { addCandidate(it) }
    }

    fun addCandidates(candidates: Collection<Candidate>) {
        candidates.forEach { addCandidate(it) }
    }

    fun getCandidates(): List<Candidate> {
        return positionToCandidate.values.toList()
    }

    fun getCandidate(position: Int): Candidate? {
        return positionToCandidate[position]
    }

    fun supportsVotesOnNomination(): Boolean {
        return supportVotesOnNomination
    }

    override fun toString(): String {
        return id.name
    }
}
