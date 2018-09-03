package vitalyros.diceformula.runtime

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.runners.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class JavaSyncRuntimeTest {
    @Mock
    lateinit var diceRoller: DiceRoller

    @Test
    fun testRollDice() {
        `when`(diceRoller.roll(20)).thenReturn(14)
        Assert.assertEquals(14, run(arrayOf(RollDice(20))))
    }

    @Test
    fun testReturnInt() {
        Assert.assertEquals(20, run(arrayOf(PushInt(20))))
    }

    fun run(commands: Array<Command>) : Any = JavaSyncRuntime(Executable(commands), diceRoller).exec()
}