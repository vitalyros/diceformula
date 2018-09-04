package vitalyros.diceformula.runtime

class RuntimeError(message: String, cause: Throwable? = null) : Exception(message, cause)

interface SyncRuntime {
    fun exec() : Any
}

class Executable(val commands: Array<Command>)

interface Command
data class PushInt(val value: Int) : Command
data class RollDice(val sides: Int) : Command
data class JoinToArray(val count: Int) : Command
class SumInts : Command
class NegateInt : Command

class SumArray : Command
class AnyArray : Command
class MaxArray : Command
class MinArray : Command
