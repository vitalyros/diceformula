package vitalyros.diceformula.syntax

import vitalyros.diceformula.parser.Expression

class SyntaxException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Builds syntactic model on the base of parser model
 * Performs syntax and type checks while doing so
 */
interface Syntax {
    fun build(e: Expression): Operation
}


