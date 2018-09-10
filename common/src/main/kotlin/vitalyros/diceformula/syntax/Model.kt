package vitalyros.diceformula.syntax

interface Operation

// Arithmetic operation
interface IntOp : Operation
data class UseIntOp(val value: Int) : IntOp
data class NegateIntOp(val op: IntOp) : IntOp
data class IntSumOp(val op1: IntOp, val op2: IntOp) : IntOp
data class MultByIntOp(val value: Int, val op: IntOp) : IntOp

// Operation that uses a dice roll or several
interface DiceOp : IntOp
data class RollDiceOp(val sides: Int) : DiceOp
// op1 or op2 or both are expected to contain a DiceOperation or a DiceOperation wrapped in NegateInt. That separates it from IntSum
data class DiceSumOp(val op1: IntOp, val op2: IntOp) : DiceOp

// Operation that returns an integer array as result
interface ArrayOp : Operation
data class PerformTimesOp(val times: Int, val op: DiceOp) : ArrayOp



// Built-in functions
data class SumFunOp(val op: ArrayOp) : IntOp
data class MaxFunOp(val op: ArrayOp) : IntOp
data class MinFunOp(val op: ArrayOp) : IntOp
data class AnyFunOp(val op: ArrayOp) : IntOp

