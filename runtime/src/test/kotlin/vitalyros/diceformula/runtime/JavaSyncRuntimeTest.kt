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
        Assert.assertEquals(14, run(arrayOf(RollDiceCmd(20))))
    }

    @Test
    fun testReturnInt() {
        Assert.assertEquals(20, run(arrayOf(PushIntCmd(20))))
    }

    @Test
    fun sumDiceAndInt() {
        `when`(diceRoller.roll(20)).thenReturn(14)
        Assert.assertEquals(26,
                run(arrayOf(
                        RollDiceCmd(20),
                        PushIntCmd(12),
                        SumIntsCmd()
                        )))
    }


    @Test
    fun sumDiceAndIntTimes() {
        `when`(diceRoller.roll(20)).thenReturn(14, 5, 7)
        Assert.assertEquals(arrayOf(19, 10, 12).asList(),
                (run(arrayOf(
                        RollDiceCmd(20),
                        PushIntCmd(5),
                        SumIntsCmd(),
                        RollDiceCmd(20),
                        PushIntCmd(5),
                        SumIntsCmd(),
                        RollDiceCmd(20),
                        PushIntCmd(5),
                        SumIntsCmd(),
                        JoinToArrayCmd(3)
                )) as Array<*>).toList())
    }

    @Test
    fun sumDiceAndIntTimesThenChooseMax() {
        `when`(diceRoller.roll(20)).thenReturn(14, 5, 7)
        Assert.assertEquals(19,
                run(arrayOf(
                        RollDiceCmd(20),
                        PushIntCmd(5),
                        SumIntsCmd(),
                        RollDiceCmd(20),
                        PushIntCmd(5),
                        SumIntsCmd(),
                        RollDiceCmd(20),
                        PushIntCmd(5),
                        SumIntsCmd(),
                        JoinToArrayCmd(3),
                        MaxArrayCmd()
                )))
    }

    @Test
    fun sumDiceAndIntTimesThenChoosAny() {
        `when`(diceRoller.roll(20)).thenReturn(14, 5, 7)
        Assert.assertTrue(
                run(arrayOf(
                        RollDiceCmd(20),
                        PushIntCmd(5),
                        SumIntsCmd(),
                        RollDiceCmd(20),
                        PushIntCmd(5),
                        SumIntsCmd(),
                        RollDiceCmd(20),
                        PushIntCmd(5),
                        SumIntsCmd(),
                        JoinToArrayCmd(3),
                        AnyArrayCmd()
                )) in arrayOf(19, 10, 12))
    }

    @Test
    fun sumDiceAndIntTimesThenChooseMin() {
        `when`(diceRoller.roll(20)).thenReturn(14, 5, 7)
        Assert.assertEquals(10,
                (run(arrayOf(
                        RollDiceCmd(20),
                        PushIntCmd(5),
                        SumIntsCmd(),
                        RollDiceCmd(20),
                        PushIntCmd(5),
                        SumIntsCmd(),
                        RollDiceCmd(20),
                        PushIntCmd(5),
                        SumIntsCmd(),
                        JoinToArrayCmd(3),
                        MinArrayCmd()
                ))))
    }




    fun run(commands: Array<Command>) : Any = JavaSyncRuntime(Executable(commands), diceRoller).exec()
}