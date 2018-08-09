package vitalyros.diceformula.parser

import vitalyros.diceformula.lexer.Token
import vitalyros.diceformula.lexer.TokenType
import java.util.*


class ParserImpl : Parser {
    private val funNameChars = ('a'..'z')
    private val intChars = ('0'..'9')
    private val pendingStack: Deque<PendingExpression> = ArrayDeque()
    private var finishedStack: Deque<Expression> = ArrayDeque()

    override fun push(token: Token) {
        when (token.Type) {
            TokenType.INT -> {
                finishedStack.push(parseInt(token))
                checkFinishedPlusMinus()
            }
            TokenType.DICE -> {
                checkMultSugar()
                finishedStack.push(parseDice(token))
                collapsePendingMult()
                checkFinishedPlusMinus()
            }
            TokenType.PLUS -> {
                collapseLastInt()
                val lastFinished = finishedStack.peek()
                pendingStack.push(PendingSum(validatePlusMinus(token, lastFinished)))
            }
            TokenType.MINUS -> {
                collapseLastInt()
                val lastFinished = finishedStack.peek()
                pendingStack.push(PendingDif(validatePlusMinus(token, lastFinished)))
            }
            TokenType.FUN_START -> {
                checkMultSugar()
                pendingStack.push(parseFun(token))
            }
            TokenType.OPEN_BRACE -> {
                checkMultSugar()
                pendingStack.push(PendingBraces(token))
            }
            TokenType.CLOSE_BRACE -> {
                checkFinishedBraces()
                collapsePendingMult()
                checkFinishedPlusMinus()
            }
            TokenType.TIMES -> {
                pendingStack.push(parseMult(token))
            }
        }
    }

    fun collapseLastInt() {
        val lastFinished = finishedStack.peek()
        if (lastFinished != null && lastFinished is IntLiteral) {
            collapsePendingMult()
        }
    }

    fun checkMultSugar() {
        val lastFinished = finishedStack.peek()
        if (lastFinished != null && lastFinished is IntLiteral) {
            finishedStack.pop()
            pendingStack.push(PendingMult(lastFinished.value))
        }
    }

    fun checkFinishedMult() {
        val exp = finishedStack.peek()
        if (exp != null && exp !is PendingExpression) {
            checkFinishedMult(exp)
        }
    }

    fun checkFinishedMult(exp: Expression) {
        if (pendingStack.size > 0) {
            val lastPending = pendingStack.peek()
            when (lastPending) {
                is PendingMult -> {
                    finishedStack.push(Mult(lastPending.mult, exp))
                    pendingStack.pop()
                }
            }
        }
    }

    fun checkFinishedBraces() {
        val exp = finishedStack.peek()
        if (exp != null && exp !is PendingExpression) {
            checkFinishedBraces(exp)
        }
    }

    fun checkFinishedBraces(exp: Expression) {
        if (pendingStack.size > 0) {
            val lastPending = pendingStack.peek()
            when (lastPending) {
                is PendingBraces -> {
                    finishedStack.push(Braces(exp))
                    pendingStack.pop()
                }
                is PendingFun -> {
                    finishedStack.push(Fun(lastPending.name, exp))
                    pendingStack.pop()
                }
            }
        }
    }

    fun collapsePendingMult() {
        var finishedCollapsing = false
        while (!finishedCollapsing && pendingStack.size > 0) {
            val depthBefore = pendingStack.size
            val lastFinished = finishedStack.peek()
            val lastPending = pendingStack.peek()
            if (lastFinished != null) {
                when (lastPending) {
                    is PendingMult -> {
                        finishedStack.push(Mult(lastPending.mult, lastFinished))
                        pendingStack.pop()
                    }
                }
            }
            val depthAfter = pendingStack.size
            finishedCollapsing = depthAfter == depthBefore
        }
    }


    fun checkFinishedPlusMinus() {
        val exp2 = finishedStack.peek()
        if (exp2 != null && exp2 !is PendingExpression) {
            checkFinishedPlusMinus(exp2)
        }
    }

    fun checkFinishedPlusMinus(exp2: Expression) {
        if (pendingStack.size > 0) {
            val lastPending = pendingStack.peek()
            when (lastPending) {
                is PendingSum -> {
                    finishedStack.push(Sum(lastPending.exp1, exp2))
                    pendingStack.pop()
                }
                is PendingDif -> {
                    finishedStack.push(Dif(lastPending.exp1, exp2))
                    pendingStack.pop()
                }
            }
        }
    }

    fun validatePlusMinus(token: Token, lastFinished: Expression?) : Expression {
        if (lastFinished != null && lastFinished !is PendingExpression) {
            return lastFinished
        } else {
            throw ParserException("Finished expression expected on the left side of ${token.str}")
        }
    }

    override fun finish(): Expression {
        System.out.println(finishedStack)
        System.out.println(pendingStack)
        collapseLastInt()

        val result = finishedStack.peek()
        if (result == null) {
            if (pendingStack.isEmpty()) {
                throw ParserException("Empty expression")
            } else {
                throw ParserException("Unfinished expression: ${pendingStack.last}}")
            }
        } else {
            return result
        }
    }

    private fun parseInt(token: Token) = IntLiteral(token.str.toInt())
    private fun parseDice(token: Token) = DiceLiteral(token.str.substring(1).toInt())
    private fun parseFun(token: Token) = PendingFun(token.str.filter { it in funNameChars })
    private fun parseMult(token: Token) = PendingMult((token.str.filter { it in intChars}).toInt())
}
private interface PendingExpression : Expression
private data class PendingSum(val exp1: Expression) : PendingExpression
private data class PendingDif(val exp1: Expression) : PendingExpression
private data class PendingFun(val name: String): PendingExpression
private data class PendingBraces(val token: Token) : PendingExpression
private data class PendingMult(val mult: Int) : PendingExpression
