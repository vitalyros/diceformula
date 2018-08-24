package vitalyros.diceformula.executor

import vitalyros.diceformula.exec.SyncExecution
import vitalyros.diceformula.translator.*
import java.util.*

class StackExecution(executable: Executable) : SyncExecution {
    val stack = ArrayDeque<Any>()
    val steps = executable.steps
    val random = Random()

    override fun exec(): Any {
        steps.forEach { step ->
            when (step) {
                is RollDice ->  stack.push(random.nextInt(step.sides) + 1)
                is PushInt -> stack.push(step.value)
                is NegateInt -> stack.push(-1 * popInt())
                is SumInts -> stack.push(popInt() + popInt())
                is AnyArray -> {
                    val array =  popIntArray()
                    stack.push(array[random.nextInt(array.size)])
                }
                is MaxArray -> stack.push(popIntArray().reduce { acc, value -> if (acc < value) value else acc })
                is MinArray -> stack.push(popIntArray().reduce { acc, value -> if (acc > value) value else acc })
                is SumArray -> stack.push(popIntArray().reduce { acc, value -> acc + value })
            }
        }
        val result = stack.pop()
        if (stack.size > 0) {
            // Throw error
        }
        if (result == null) {
            // Throw error
        }
        if (result !is Array<*> || result !is Int) {
            // Throw error
        }
        return result
    }

    fun popInt() : Int {
        val value = stack.pop()
        if (value is Int) {
            return value
        } else {
            // Throw error
            throw Exception()
        }
    }

    fun popIntArray() : Array<Int> {
        val array = stack.pop()
        if (array is Array<*>) {
            return array as Array<Int>
        } else {
            throw Exception()
        }
    }
}
