package de.twomartens.wahlrecht.model.internal

enum class VotingThreshold(val multiplier: Double) {
    NONE(0.00),
    THREE(0.03),
    FIVE(0.05)
}