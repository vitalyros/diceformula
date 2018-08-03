package vitalyros.diceformula.common

data class Token(val Type : TokenType, val col: Int, val str: String)

enum class TokenType {
    DICE,
    INT,
    PLUS,
    MINUS,
    TIMES,
    OPEN_BRACE,
    CLOSE_BRACE,
    FUN_START
}