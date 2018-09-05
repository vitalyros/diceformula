package vitalyros.diceformula.runtime

class RuntimeError(message: String, cause: Throwable? = null) : Exception(message, cause)

interface SyncRuntime {
    fun exec() : Any
}

class Executable(val commands: Array<Command>)

interface Command
data class PushIntCommand(val value: Int) : Command
data class RollDiceCommand(val sides: Int) : Command
data class JoinToArrayCommand(val count: Int) : Command
class MultIntsCommand : Command
class SumIntsCommand : Command
class NegateIntCommand : Command

class SumArrayCommand : Command
class AnyArrayCommand : Command
class MaxArrayCommand : Command
class MinArrayCommand : Command
