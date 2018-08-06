package vitalyros.diceformula.parser

import vitalyros.diceformula.lexer.Token
import vitalyros.diceformula.lexer.TokenType
import java.util.*


class ParserImpl : Parser {
    private val funNameChars = ('a'..'z')
    private val intChars = ('0' .. '9')
    private val pendingStack: Deque<PendingExpression> = ArrayDeque()
    private var lastFinished: Expression? = null

    override fun push(token: Token) {
        when (token.Type) {
            TokenType.INT -> {
                lastFinished = parseInt(token)
                checkFinishedPlusMinus()
            }
            TokenType.DICE -> {
                lastFinished = parseDice(token)
                checkFinishedPlusMinus()
                checkFinishedMult()
            }
            TokenType.PLUS -> {
                pendingStack.push(PendingSum(validatePlusMinus(token, lastFinished)))
            }
            TokenType.MINUS -> {
                pendingStack.push(PendingDif(validatePlusMinus(token, lastFinished)))
            }
            TokenType.FUN_START -> {
                pendingStack.push(parseFun(token))
            }
            TokenType.OPEN_BRACE -> {
                pendingStack.push(PendingBraces(token))
            }
            TokenType.CLOSE_BRACE -> {
                checkFinishedBraces()
                checkFinishedPlusMinus()
                checkFinishedMult()
            }
            TokenType.MULT -> {
                pendingStack.push(parseMult(token))
            }
        }
    }

    fun checkFinishedMult() {
        val exp = lastFinished
        if (exp != null && exp !is PendingExpression) {
            checkFinishedMult(exp)
        }
    }


    fun checkFinishedMult(exp: Expression) {
        if (pendingStack.size > 0) {
            val lastPending = pendingStack.peek()
            when (lastPending) {
                is PendingMult -> {
                    this.lastFinished = Mult(lastPending.mult, exp)
                    pendingStack.pop()
                }
            }
        }
    }



    fun checkFinishedBraces() {
        val exp = lastFinished
        if (exp != null && exp !is PendingExpression) {
            checkFinishedBraces(exp)
        }
    }

    fun checkFinishedBraces(exp: Expression) {
        if (pendingStack.size > 0) {
            val lastPending = pendingStack.peek()
            when (lastPending) {
                is PendingBraces -> {
                    this.lastFinished = Braces(exp)
                    pendingStack.pop()
                }
                is PendingFun -> {
                    this.lastFinished = Fun(lastPending.name, exp)
                    pendingStack.pop()
                }
            }
        }
    }


    fun checkFinishedPlusMinus() {
        val exp2 = lastFinished
        if (exp2 != null && exp2 !is PendingExpression) {
            checkFinishedPlusMinus(exp2)
        }
    }

    fun checkFinishedPlusMinus(exp2: Expression) {
        if (pendingStack.size > 0) {
            val lastPending = pendingStack.peek()
            when (lastPending) {
                is PendingSum -> {
                    this.lastFinished = Sum(lastPending.exp1, exp2)
                    pendingStack.pop()
                }
                is PendingDif -> {
                    this.lastFinished = Dif(lastPending.exp1, exp2)
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
        val result = lastFinished
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
