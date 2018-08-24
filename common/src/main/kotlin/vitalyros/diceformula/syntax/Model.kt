package vitalyros.diceformula.syntax

interface Operation
interface IntOperation : Operation
interface DiceOperation : IntOperation
interface ArrayOperation : Operation

data class RollDice(val sides: Int) : DiceOperation
data class UseInt(val value: Int) : IntOperation
data class NegateInt(val op: IntOperation) : IntOperation
data class IntSum(val op1: IntOperation, val op2: IntOperation) : IntOperation
data class DiceSum(val op1: IntOperation, val op2: IntOperation) : DiceOperation
data class MultByInt(val value: Int, val op: IntOperation) : IntOperation
data class PerformTimes(val times: Int, val op: DiceOperation) : ArrayOperation

data class SumFun(val op: ArrayOperation) : IntOperation
data class MaxFun(val op: ArrayOperation) : IntOperation
data class MinFun(val op: ArrayOperation) : IntOperation
data class AnyFun(val op: ArrayOperation) : IntOperation

