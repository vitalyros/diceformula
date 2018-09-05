package vitalyros.diceformula.syntax

interface Operation

// Arithmetic operation
interface IntOperation : Operation
data class UseIntOperation(val value: Int) : IntOperation
data class NegateIntOperation(val op: IntOperation) : IntOperation
data class IntSumOperation(val op1: IntOperation, val op2: IntOperation) : IntOperation
data class MultByIntOperation(val value: Int, val op: IntOperation) : IntOperation

// Operation that uses a dice roll or several
interface DiceOperation : IntOperation
data class RollDiceOperation(val sides: Int) : DiceOperation
// op1 or op2 or both are expected to contain a DiceOperation or a DiceOperation wrapped in NegateInt. That separates it from IntSum
data class DiceSumOperation(val op1: IntOperation, val op2: IntOperation) : DiceOperation

// Operation that returns an integer array as result
interface ArrayOperation : Operation
data class PerformTimesOperation(val times: Int, val op: DiceOperation) : ArrayOperation



// Built-in functions
data class SumFunOperation(val op: ArrayOperation) : IntOperation
data class MaxFunOperation(val op: ArrayOperation) : IntOperation
data class MinFunOperation(val op: ArrayOperation) : IntOperation
data class AnyFunOperation(val op: ArrayOperation) : IntOperation

