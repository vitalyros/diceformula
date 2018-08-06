package vitalyros.diceformula.parser

import vitalyros.diceformula.lexer.Token

class ParserException(message: String, cause: Throwable? = null) : Exception(message, cause)

interface Parser {
    fun push(token: Token)
    fun finish() : Expression
}

