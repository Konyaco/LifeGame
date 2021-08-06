package game

import kotlin.random.Random
import kotlin.test.Test

internal class ChessImplTest {
    @Test
    fun test() {
        val chess = ChessImpl()
        repeat(10000) {
            val x = Random.nextInt(0, 100)
            val y = Random.nextInt(0, 100)
            val state = if(Random.nextBoolean()) Chess.State.LIVING else Chess.State.DEAD
            chess[x, y] = state
            assert(chess[x, y] == state)
        }
    }
}