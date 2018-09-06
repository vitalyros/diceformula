package vitalyros.diceformula.core

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.runners.MockitoJUnitRunner
import vitalyros.diceformula.runtime.DiceRoller

@RunWith(MockitoJUnitRunner::class)
class DiceFormulaTest {
    @Mock
    lateinit var diceRoller: DiceRoller

    @Test
    fun simpleTest() {
        `when`(diceRoller.roll(20)).thenReturn(10)
        `when`(diceRoller.roll(6)).thenReturn(3)
        Assert.assertEquals(18, DiceFormula.exec("d20 + d6 + 5".toByteArray(), diceRoller))
    }
}