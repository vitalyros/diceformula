package vitalyros.diceformula.translator

import vitalyros.diceformula.runtime.Executable
import vitalyros.diceformula.syntax.Operation

interface Translator {
    fun translate(operation: Operation) : Executable
}

