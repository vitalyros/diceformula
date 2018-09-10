package vitalyros.diceformula.runtime

class RuntimeError(message: String, cause: Throwable? = null) : Exception(message, cause)

interface SyncRuntime {
    fun exec() : Any
}

class Executable(val commands: Array<Command>)

interface Command
data class PushIntCmd(val value: Int) : Command
data class RollDiceCmd(val sides: Int) : Command
data class JoinToArrayCmd(val count: Int) : Command
class MultIntsCmd : Command
class SumIntsCmd : Command
class NegateIntCmd : Command

class SumArrayCmd : Command
class AnyArrayCmd : Command
class MaxArrayCmd : Command
class MinArrayCmd : Command
