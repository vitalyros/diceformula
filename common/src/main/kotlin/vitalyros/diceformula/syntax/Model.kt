package vitalyros.diceformula.syntax

interface Operation

// Arithmetic operation
interface IntOperation : Operation
data class UseInt(val value: Int) : IntOperation
data class NegateInt(val op: IntOperation) : IntOperation
data class IntSum(val op1: IntOperation, val op2: IntOperation) : IntOperation
data class MultByInt(val value: Int, val op: IntOperation) : IntOperation

// Operation that uses a dice roll or several
interface DiceOperation : IntOperation
data class RollDice(val sides: Int) : DiceOperation
// op1 or op2 or both are expected to contain a DiceOperation or a DiceOperation wrapped in NegateInt. That separates it from IntSum
data class DiceSum(val op1: IntOperation, val op2: IntOperation) : DiceOperation

// Operation that returns an integer array as result
interface ArrayOperation : Operation
data class PerformTimes(val times: Int, val op: DiceOperation) : ArrayOperation



// Built-in functions
data class SumFun(val op: ArrayOperation) : IntOperation
data class MaxFun(val op: ArrayOperation) : IntOperation
data class MinFun(val op: ArrayOperation) : IntOperation
data class AnyFun(val op: ArrayOperation) : IntOperation

