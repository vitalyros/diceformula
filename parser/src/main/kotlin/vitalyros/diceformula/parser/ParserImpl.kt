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
                checkMultSugar(token)
                pushFinishedExpression(parseDice(token))
                collapseMult()
                collapsePlusMinus()
            }
            TokenType.PLUS -> {
                collapseIntMult()
                collapsePlusMinus()
                val lastFinished = finishedStack.peek()
                pushPendingExpression(PendingSum(token, validatePlusMinus(token, lastFinished)))
            }
            TokenType.MINUS -> {
                collapseIntMult()
                collapsePlusMinus()
                val lastFinished = finishedStack.peek()
                pushPendingExpression(PendingDif(token, validatePlusMinus(token, lastFinished)))
            }
            TokenType.FUN_START -> {
                checkMultSugar(token)
                pushPendingExpression(parseFun(token))
            }
            TokenType.OPEN_BRACE -> {
                checkMultSugar(token)
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
                throw buildParserException(index = 0, reason = "Empty line found instead of expression", flavour = "Empty line is not considered a valid expression to compile or run")
            } else {
                val token = pendingStack.last.token
                throw buildParserException(index = token.col, str = token.str, reason = "Unfinished expression")
            }
        } else {
            return result
        }
    }

    /**
     * Checks the last finished expression to be int and collapses all preceding unfinished multiplication expressions, if they exist
     */
    private fun collapseIntMult() {
        val lastFinished = finishedStack.peek()
        if (lastFinished != null && lastFinished is IntLiteralExpr) {
            collapseMult()
        }
    }


    /**
     * Checks the last finished expression to be int and substitutes it for unfinished multiplication expression
     */
    private fun checkMultSugar(token: Token) {
        val lastFinished = finishedStack.peek()
        if (lastFinished != null && lastExpression != null && lastExpression == lastFinished && lastFinished is IntLiteralExpr) {
            finishedStack.pop()
            pushPendingExpression(PendingMult(token, lastFinished.value))
        }
    }

    /**
     * Closes immediate pending open braces or function expressions
     */
    private fun collapseBraces() {
        val exp = finishedStack.peek()
        if (exp != null) {
            if (pendingStack.size > 0) {
                val lastPending = pendingStack.peek()
                when (lastPending) {
                    is PendingBraces -> {
                        pushFinishedExpression(BracesExpr(exp))
                        pendingStack.pop()
                    }
                    is PendingFun -> {
                        pushFinishedExpression(FunExpr(lastPending.name, exp))
                        pendingStack.pop()
                    }
                }
            }
        }
    }

    /**
     * Collapses the chain of unfinished multiplication expressions
     */
    private fun collapseMult() {
        var finishedCollapsing = false
        while (!finishedCollapsing && pendingStack.size > 0) {
            val depthBefore = pendingStack.size
            val lastFinished = finishedStack.peek()
            val lastPending = pendingStack.peek()
            if (lastFinished != null) {
                when (lastPending) {
                    is PendingMult -> {
                        pushFinishedExpression(MultExpr(lastPending.mult, lastFinished))
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
    private fun collapsePlusMinus() {
        val exp2 = finishedStack.peek()
        if (exp2 != null) {
            if (pendingStack.size > 0) {
                val lastPending = pendingStack.peek()
                when (lastPending) {
                    is PendingSum -> {
                        pushFinishedExpression(SumExpr(lastPending.exp1, exp2))
                        pendingStack.pop()
                    }
                    is PendingDif -> {
                        pushFinishedExpression(SumExpr(lastPending.exp1, NegExpr(exp2)))
                        pendingStack.pop()
                    }
                }
            }
        }
    }

    private fun validatePlusMinus(token: Token, lastFinished: Expression?): Expression {
        return if (lastFinished == null) {
            throw buildParserException(index = token.col, reason = "Left side of \"${token.col}\" is empty, expression expected")
        } else if (lastFinished is PendingExpression) {
            throw buildParserException(index = token.col, reason = "Left side of \"${token.col}\" is an unfinished expression")
        } else {
            lastFinished
        }
    }

    private fun parseInt(token: Token) = IntLiteralExpr(token.str.toInt())
    private fun parseDice(token: Token) = DiceLiteralExpr(token.str.substring(1).toInt())
    private fun parseFun(token: Token) = PendingFun(token, token.str.filter { it in funNameChars })
    private fun parseMult(token: Token) = PendingMult(token, (token.str.filter { it in intChars }).toInt())
}

fun buildParserException(index: Int? = null, str: String? = null, reason: String? = null, flavour: String? = null) : ParserException {
    val indexExpression = if (index == null) {
        ""
    } else {
        " at index: $index"
    }
    val tokenExpression = if (str == null) {
        ""
    } else {
        "Failed to parse token: $str. "
    }
    val reasonExpression = if (reason == null) {
        ""
    } else {
        "Reason: $reason."
    }
    val flavourExpression = if (flavour == null) {
        ""
    } else {
        "\n$flavour"
    }
    return ParserException("Parser Error$indexExpression. $tokenExpression$reasonExpression$flavourExpression")
}

private interface PendingExpression : Expression {
    val token: Token
}
private data class PendingSum(override val token: Token, val exp1: Expression) : PendingExpression
private data class PendingDif(override val token: Token, val exp1: Expression) : PendingExpression
private data class PendingFun(override val token: Token, val name: String) : PendingExpression
private data class PendingBraces(override val token: Token) : PendingExpression
private data class PendingMult(override val token: Token, val mult: Int) : PendingExpression
