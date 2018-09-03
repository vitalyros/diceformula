package vitalyros.diceformula.runtime

import java.util.*

interface DiceRoller {
    fun roll(sides: Int) : Int
}

class SimpleDiceRoller : DiceRoller {
    val random = Random()

    override fun roll(sides: Int): Int = random.nextInt(sides) + 1
}