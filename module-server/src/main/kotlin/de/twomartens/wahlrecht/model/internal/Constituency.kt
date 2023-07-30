package de.twomartens.wahlrecht.model.internal

data class Constituency(val electionName: String, val number: Int, val name: String, val numberOfSeats: Int) {
    override fun toString(): String {
        return name
    }
}
