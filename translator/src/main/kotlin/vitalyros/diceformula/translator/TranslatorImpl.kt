package vitalyros.diceformula.translator

import vitalyros.diceformula.runtime.*
import vitalyros.diceformula.syntax.*

class TranslatorImpl() : Translator {
    override fun translate(operation: Operation): Executable {
        return Executable(doTranslate(operation).toTypedArray())
    }

    private fun doTranslate(operation: Operation) : MutableList<Command> {
        val acc = ArrayList<Command>()
        doTranslate(operation, acc)
        return acc
    }

    private fun doTranslate(operation: Operation, acc: MutableList<Command>) {
        when(operation) {
            is UseIntOperation -> acc.add(PushIntCommand(operation.value))
            is NegateIntOperation -> {
                doTranslate(operation.op, acc)
                acc.add(NegateIntCommand())
            }
            is IntSumOperation -> {
                doTranslate(operation.op1, acc)
                doTranslate(operation.op2, acc)
                acc.add(SumIntsCommand())
            }
            is DiceSumOperation -> {
                doTranslate(operation.op1, acc)
                doTranslate(operation.op2, acc)
                acc.add(SumIntsCommand())
            }
            is MultByIntOperation -> {
                doTranslate(operation.op, acc)
                acc.add(PushIntCommand(operation.value))
                acc.add(MultIntsCommand())
            }
            is RollDiceOperation -> acc.add(RollDiceCommand(operation.sides))
            is PerformTimesOperation -> {
                val performTimesAcc = doTranslate(operation.op)
                repeat(operation.times) {
                    acc.addAll(performTimesAcc)
                }
                acc.add(JoinToArrayCommand(operation.times))
            }
            is SumFunOperation -> {
                doTranslate(operation.op, acc)
                acc.add(SumArrayCommand())
            }
            is AnyFunOperation -> {
                doTranslate(operation.op, acc)
                acc.add(AnyArrayCommand())
            }
            is MaxFunOperation -> {
                doTranslate(operation.op, acc)
                acc.add(MaxArrayCommand())
            }
            is MinFunOperation -> {
                doTranslate(operation.op, acc)
                acc.add(MinArrayCommand())
            }
        }
    }
}