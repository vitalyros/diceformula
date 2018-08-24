package vitalyros.diceformula.parser

import vitalyros.diceformula.lexer.Token
import vitalyros.diceformula.lexer.TokenType
import java.util.*


class ParserImpl : Parser {
    private val funNameChars = ('a'..'z')
    private val intChars = ('0'..'9')
    private var lastExpression: Expression? = null
    private val pendingStack: Deque<PendingExpression> = ArrayDeque()
    private var finishedStack: Deque<Expression> = ArrayDeque()

    private fun pushPendingExpression(expression: PendingExpression) {
        pendingStack.push(expression)
        lastExpression = expression
    }

    private fun pushFinishedExpression(expression: Expression) {
        finishedStack.push(expression)
        lastExpression = expression
    }

    override fun push(token: Token) {
        when (token.Type) {
            TokenType.INT -> {
                pushFinishedExpression(parseInt(token))
            }
            TokenType.DICE -> {
                checkMultSugar()
                pushFinishedExpression(parseDice(token))
                collapseMult()
                collapsePlusMinus()
            }
            TokenType.PLUS -> {
                collapseIntMult()
                collapsePlusMinus()
                val lastFinished = finishedStack.peek()
                pushPendingExpression(PendingSum(validatePlusMinus(token, lastFinished)))
            }
            TokenType.MINUS -> {
                collapseIntMult()
                collapsePlusMinus()
                val lastFinished = finishedStack.peek()
                pushPendingExpression(PendingDif(validatePlusMinus(token, lastFinished)))
            }
            TokenType.FUN_START -> {
                checkMultSugar()
                pushPendingExpression(parseFun(token))
            }
            TokenType.OPEN_BRACE -> {
                checkMultSugar()
                pushPendingExpression(PendingBraces(token))
            }
            TokenType.CLOSE_BRACE -> {
                collapseIntMult()
                collapsePlusMinus()
                collapseBraces()
                collapseMult()
                collapsePlusMinus()
            }
            TokenType.TIMES -> {
                pushPendingExpression(parseMult(token))
            }
        }
    }

    override fun finish(): Expression {
        // Expression may end on unprocessed int literal, process it here
        collapseIntMult()
        collapsePlusMinus()
        // The top of the finished stack should be the result
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

    /**
     * Checks the last finished expression to be int and collapses all preceding unfinished multiplication expressions, if they exist
     */
    fun collapseIntMult() {
        val lastFinished = finishedStack.peek()
        if (lastFinished != null && lastFinished is IntLiteral) {
            collapseMult()
        }
    }


    /**
     * Checks the last finished expression to be int and substitutes it for unfinished multiplication expression
     */
    fun checkMultSugar() {
        val lastFinished = finishedStack.peek()
        if (lastFinished != null && lastExpression != null && lastExpression == lastFinished && lastFinished is IntLiteral) {
            finishedStack.pop()
            pushPendingExpression(PendingMult(lastFinished.value))
        }
    }

    /**
     * Closes immediate pending open braces or function expressions
     */
    fun collapseBraces() {
        val exp = finishedStack.peek()
        if (exp != null) {
            if (pendingStack.size > 0) {
                val lastPending = pendingStack.peek()
                when (lastPending) {
                    is PendingBraces -> {
                        pushFinishedExpression(Braces(exp))
                        pendingStack.pop()
                    }
                    is PendingFun -> {
                        pushFinishedExpression(Fun(lastPending.name, exp))
                        pendingStack.pop()
                    }
                }
            }
        }
    }

    /**
     * Collapses the chain of unfinished multiplication expressions
     */
    fun collapseMult() {
        var finishedCollapsing = false
        while (!finishedCollapsing && pendingStack.size > 0) {
            val depthBefore = pendingStack.size
            val lastFinished = finishedStack.peek()
            val lastPending = pendingStack.peek()
            if (lastFinished != null) {
                when (lastPending) {
                    is PendingMult -> {
                        pushFinishedExpression(Mult(lastPending.mult, lastFinished))
                        pendingStack.pop()
                    }
                }
            }
            val depthAfter = pendingStack.size
            finishedCollapsing = depthAfter == depthBefore
        }
    }

    /**
     * Closes immediate pending sum or dif expression
     */
    fun collapsePlusMinus() {
        val exp2 = finishedStack.peek()
        if (exp2 != null) {
            if (pendingStack.size > 0) {
                val lastPending = pendingStack.peek()
                when (lastPending) {
                    is PendingSum -> {
                        pushFinishedExpression(Sum(lastPending.exp1, exp2))
                        pendingStack.pop()
                    }
                    is PendingDif -> {
                        pushFinishedExpression(Sum(lastPending.exp1, Neg(exp2)))
                        pendingStack.pop()
                    }
                }
            }
        }
    }

    fun validatePlusMinus(token: Token, lastFinished: Expression?): Expression {
        if (lastFinished != null && lastFinished !is PendingExpression) {
            return lastFinished
        } else {
            throw ParserException("Finished expression expected on the left side of ${token.str}")
        }
    }


    private fun parseInt(token: Token) = IntLiteral(token.str.toInt())
    private fun parseDice(token: Token) = DiceLiteral(token.str.substring(1).toInt())
    private fun parseFun(token: Token) = PendingFun(token.str.filter { it in funNameChars })
    private fun parseMult(token: Token) = PendingMult((token.str.filter { it in intChars }).toInt())
}

private interface PendingExpression : Expression
private data class PendingSum(val exp1: Expression) : PendingExpression
private data class PendingDif(val exp1: Expression) : PendingExpression
private data class PendingFun(val name: String) : PendingExpression
private data class PendingBraces(val token: Token) : PendingExpression
private data class PendingMult(val mult: Int) : PendingExpression
