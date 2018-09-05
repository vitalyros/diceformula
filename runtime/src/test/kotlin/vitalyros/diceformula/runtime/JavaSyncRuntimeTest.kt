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
        Assert.assertEquals(14, run(arrayOf(RollDiceCommand(20))))
    }

    @Test
    fun testReturnInt() {
        Assert.assertEquals(20, run(arrayOf(PushIntCommand(20))))
    }

    @Test
    fun sumDiceAndInt() {
        `when`(diceRoller.roll(20)).thenReturn(14)
        Assert.assertEquals(26,
                run(arrayOf(
                        RollDiceCommand(20),
                        PushIntCommand(12),
                        SumIntsCommand()
                        )))
    }


    @Test
    fun sumDiceAndIntTimes() {
        `when`(diceRoller.roll(20)).thenReturn(14, 5, 7)
        Assert.assertEquals(arrayOf(19, 10, 12).asList(),
                (run(arrayOf(
                        RollDiceCommand(20),
                        PushIntCommand(5),
                        SumIntsCommand(),
                        RollDiceCommand(20),
                        PushIntCommand(5),
                        SumIntsCommand(),
                        RollDiceCommand(20),
                        PushIntCommand(5),
                        SumIntsCommand(),
                        JoinToArrayCommand(3)
                )) as Array<*>).toList())
    }

    @Test
    fun sumDiceAndIntTimesThenChooseMax() {
        `when`(diceRoller.roll(20)).thenReturn(14, 5, 7)
        Assert.assertEquals(19,
                run(arrayOf(
                        RollDiceCommand(20),
                        PushIntCommand(5),
                        SumIntsCommand(),
                        RollDiceCommand(20),
                        PushIntCommand(5),
                        SumIntsCommand(),
                        RollDiceCommand(20),
                        PushIntCommand(5),
                        SumIntsCommand(),
                        JoinToArrayCommand(3),
                        MaxArrayCommand()
                )))
    }

    @Test
    fun sumDiceAndIntTimesThenChoosAny() {
        `when`(diceRoller.roll(20)).thenReturn(14, 5, 7)
        Assert.assertTrue(
                run(arrayOf(
                        RollDiceCommand(20),
                        PushIntCommand(5),
                        SumIntsCommand(),
                        RollDiceCommand(20),
                        PushIntCommand(5),
                        SumIntsCommand(),
                        RollDiceCommand(20),
                        PushIntCommand(5),
                        SumIntsCommand(),
                        JoinToArrayCommand(3),
                        AnyArrayCommand()
                )) in arrayOf(19, 10, 12))
    }

    @Test
    fun sumDiceAndIntTimesThenChooseMin() {
        `when`(diceRoller.roll(20)).thenReturn(14, 5, 7)
        Assert.assertEquals(10,
                (run(arrayOf(
                        RollDiceCommand(20),
                        PushIntCommand(5),
                        SumIntsCommand(),
                        RollDiceCommand(20),
                        PushIntCommand(5),
                        SumIntsCommand(),
                        RollDiceCommand(20),
                        PushIntCommand(5),
                        SumIntsCommand(),
                        JoinToArrayCommand(3),
                        MinArrayCommand()
                ))))
    }




    fun run(commands: Array<Command>) : Any = JavaSyncRuntime(Executable(commands), diceRoller).exec()
}