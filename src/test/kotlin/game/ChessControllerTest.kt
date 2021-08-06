package game

import kotlin.random.Random
import kotlin.test.Test

internal class ChessControllerTest {
    @Test
    fun testPlacement() {
        val controller = ChessController()
        var lastX = 0
        var lastY = 0
        var lastState = Chess.State.DEAD

        repeat(10000) {
            assert(controller.get(lastX, lastY) == lastState)

            val x = Random.nextInt(0, 100)
            val y = Random.nextInt(0, 100)
            val state = if (Random.nextBoolean()) Chess.State.LIVING else Chess.State.DEAD

            controller.set(x, y, state)

            assert(controller.get(x, y) == state)

            lastX = x
            lastY = y
            lastState = state
        }
    }

    @Test
    fun testNeighbors() {
        val controller = ChessController()
        controller.set(0, 0, Chess.State.LIVING)
        controller.set(1, 0, Chess.State.LIVING)
        controller.set(2, 0, Chess.State.LIVING)
        controller.set(0, 1, Chess.State.LIVING)
        assert(controller.liveNeighbors(controller.getChess(), 1, 1) == 4)
    }

    @Test
    fun testRound() {
        val controller = ChessController()
        controller.put(0, 0)
        assert(controller.get(0, 0) == Chess.State.LIVING)
        controller.nextRound()
        assert(controller.get(0, 0) == Chess.State.DEAD)

        // Stable
        controller.apply {
            put(0, 0)
            put(0, 1)
            put(1, 0)
            put(1, 1)
            println(controller.getChess())
            controller.nextRound()
        }

        with(controller) {
            assert(get(0, 0) == Chess.State.LIVING)
            assert(get(0, 1) == Chess.State.LIVING)
            assert(get(1, 0) == Chess.State.LIVING)
            assert(get(1, 1) == Chess.State.LIVING)
            println(controller.getChess())
        }

        // Unstable
        controller.apply {
            put(11, 10)
            put(11, 11)
            put(11, 12)
            println(controller.getChess())
            controller.nextRound()
        }

        with(controller) {
            assert(get(11,10) == Chess.State.DEAD)
            assert(get(10,11) == Chess.State.LIVING)
            assert(get(11,11) == Chess.State.LIVING)
            assert(get(12,11) == Chess.State.LIVING)
            println(controller.getChess())
        }
    }
}