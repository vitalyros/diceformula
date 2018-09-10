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
            is UseIntOp -> acc.add(PushIntCmd(operation.value))
            is NegateIntOp -> {
                doTranslate(operation.op, acc)
                acc.add(NegateIntCmd())
            }
            is IntSumOp -> {
                doTranslate(operation.op1, acc)
                doTranslate(operation.op2, acc)
                acc.add(SumIntsCmd())
            }
            is DiceSumOp -> {
                doTranslate(operation.op1, acc)
                doTranslate(operation.op2, acc)
                acc.add(SumIntsCmd())
            }
            is MultByIntOp -> {
                doTranslate(operation.op, acc)
                acc.add(PushIntCmd(operation.value))
                acc.add(MultIntsCmd())
            }
            is RollDiceOp -> acc.add(RollDiceCmd(operation.sides))
            is PerformTimesOp -> {
                val performTimesAcc = doTranslate(operation.op)
                repeat(operation.times) {
                    acc.addAll(performTimesAcc)
                }
                acc.add(JoinToArrayCmd(operation.times))
            }
            is SumFunOp -> {
                doTranslate(operation.op, acc)
                acc.add(SumArrayCmd())
            }
            is AnyFunOp -> {
                doTranslate(operation.op, acc)
                acc.add(AnyArrayCmd())
            }
            is MaxFunOp -> {
                doTranslate(operation.op, acc)
                acc.add(MaxArrayCmd())
            }
            is MinFunOp -> {
                doTranslate(operation.op, acc)
                acc.add(MinArrayCmd())
            }
        }
    }
}