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

    @Test
    fun functionTest_max() {
        `when`(diceRoller.roll(20)).thenReturn(10,  15, 6)
        `when`(diceRoller.roll(6)).thenReturn(3, 6, 5)
        Assert.assertEquals(26, DiceFormula.exec("max(3 * (d20 + d6 + 5))".toByteArray(), diceRoller))
    }
    @Test
    fun functionTest_min() {
        `when`(diceRoller.roll(20)).thenReturn(10,  15, 6)
        `when`(diceRoller.roll(6)).thenReturn(3, 6, 5)
        Assert.assertEquals(16, DiceFormula.exec("min(3 * (d20 + d6 + 5))".toByteArray(), diceRoller))
    }
}