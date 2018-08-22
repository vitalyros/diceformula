package vitalyros.diceformula.syntax

import vitalyros.diceformula.parser.*

class SyntaxImpl : Syntax {
    override fun build(e: Expression): Operation {
        return when(e) {
            is DiceLiteral -> RollDice(e.sides)
            is IntLiteral -> UseInt(e.value)

            is Sum -> buildSum(e)
            is Dif -> buildDif(e)
            is Fun -> buildFun(e)

            is Mult -> buildMult(e)
            is Braces -> build(e.exp)

            else -> throw SyntaxException("Unexpected expression $e")
        }
    }

    private fun buildFun(e : Fun) : Operation {
        val op = build(e.exp)
        return when (e.name) {
            "sum" -> SumFun(checkFunTypeArray(e, op))
            "max" -> MaxFun(checkFunTypeArray(e, op))
            "min" -> MinFun(checkFunTypeArray(e, op))
            "any" -> AnyFun(checkFunTypeArray(e, op))
            else -> throw SyntaxException("Unexpected function $e")
        }
    }

    private fun checkFunTypeArray(e : Fun, op: Operation) : ArrayOperation {
        if (op !is ArrayOperation) {
            throw SyntaxException("Expected the nested expression for function ${e.name} to return array.")
        } else {
            return op
        }
    }

    private fun buildMult(e : Mult) : Operation {
        val op = build(e.exp)
        return when (op) {
            is DiceOperation -> PerformTimes(e.multiplier, op)
            is IntOperation -> MultByInt(e.multiplier, op)
            is ArrayOperation -> throw SyntaxException("Right side of a multiplication expected to return integer. ${e.exp}")
            else ->  throw SyntaxException("Unknown type of the left side operation for multiplication ${e.exp}")
        }
    }

    private fun buildSum(e : Sum) : Operation {
        val op1 = build(e.exp1)
        val op2 = build(e.exp2)
        if (op1 !is IntOperation) {
            throw SyntaxException("Left side of a sum expected to return integer. ${e.exp1}")
        }
        if (op2 !is IntOperation) {
            throw SyntaxException("Right side of a sum expected to return integer. ${e.exp2}")
        }
        return if (op1 is DiceOperation || op2 is DiceOperation) {
            DiceSum(op1, op2)
        } else {
            IntSum(op1, op2)
        }
    }

    private fun buildDif(e : Dif) : Operation {
        val op1 = build(e.exp1)
        val op2 = build(e.exp2)
        if (op1 !is IntOperation) {
            throw SyntaxException("Left side of a dif expected to return integer. ${e.exp1}")
        }
        if (op2 !is IntOperation) {
            throw SyntaxException("Right side of a dif expected to return integer. ${e.exp2}")
        }
        return if (op1 is DiceOperation || op2 is DiceOperation) {
            DiceSum(op1, op2)
        } else {
            IntSum(op1, UseNegativeInt(op2))
        }
    }
}