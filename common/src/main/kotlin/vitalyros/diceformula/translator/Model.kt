package vitalyros.diceformula.translator

import vitalyros.diceformula.syntax.Operation

interface Translator {
    fun translate(operation: Operation) : Executable
}

class Executable(val steps: Array<Step>)

interface Step
data class PushInt(val value: Int) : Step
data class RollDice(val sides: Int) : Step
class SumInts : Step
class NegateInt : Step

class SumArray : Step
class AnyArray : Step
class MaxArray : Step
class MinArray : Step
