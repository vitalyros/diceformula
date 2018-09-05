package vitalyros.diceformula.runtime

import java.util.*

class JavaSyncRuntime(executable: Executable, val diceRoller: DiceRoller) : SyncRuntime {
    val stack = ArrayDeque<Any>()
    val commands = executable.commands
    val random = Random()

    override fun exec(): Any {
        commands.forEach { command ->
            when (command) {
                is RollDiceCommand ->  stack.push(diceRoller.roll(command.sides))
                is PushIntCommand -> stack.push(command.value)
                is NegateIntCommand -> stack.push(-1 * popInt())
                is SumIntsCommand -> stack.push(popInt() + popInt())
                is MultIntsCommand -> stack.push(popInt() * popInt())
                is JoinToArrayCommand -> {
                    val result = (0 .. command.count - 1).map {
                        popInt()
                    }.toTypedArray()
                    result.reverse()
                    stack.push(result)
                }
                is AnyArrayCommand -> {
                    val array =  popIntArray()
                    stack.push(array[random.nextInt(array.size)])
                }
                is MaxArrayCommand -> stack.push(popIntArray().reduce { acc, value -> if (acc < value) value else acc })
                is MinArrayCommand -> stack.push(popIntArray().reduce { acc, value -> if (acc > value) value else acc })
                is SumArrayCommand -> stack.push(popIntArray().reduce { acc, value -> acc + value })
            }
        }
        val result = stack.pop()
        if (stack.size > 0) {
            throw RuntimeError("Unexpected finishing state. Stack not empty after execution finished. Stack: $stack")
        }
        if (result == null) {
            throw RuntimeError("Unexpected finishing state. Empty execution result.")
        }
        if (result !is Array<*> && result !is Int) {
            throw RuntimeError("Unexpected finishing state. Unexpected execution result type. Result: $result")
        }
        return result
    }

    fun popInt() : Int {
        val value = stack.pop()
        if (value is Int) {
            return value
        } else {
            throw RuntimeError("Unexpected type popped. Expected ${Int::class.java}, got ${value.javaClass}")
        }
    }

    fun popIntArray() : Array<Int> {
        val value = stack.pop()
        if (value is Array<*>) {
            return value as Array<Int>
        } else {
            throw RuntimeError("Unexpected type popped. Expected ${Array<Int>::class.java}, got ${value.javaClass}")
        }
    }
}
