package game

class ChessController {
    private var chess: ChessData = ChessData()

    fun getChess(): Chess {
        return chess
    }

    fun newChess() {
        chess = ChessData(chess.height, chess.width)
    }

    fun nextRound() {
        val newChess = chess.copy()

        chess.forEachCell { x, y, cell ->
            val liveNeighbors = liveNeighbors(chess, x, y)
            when {
                cell == Chess.State.LIVING && liveNeighbors < 2 ->
                    newChess[x, y] = Chess.State.DEAD // Living cell come to die
                cell == Chess.State.LIVING && liveNeighbors > 3 ->
                    newChess[x, y] = Chess.State.DEAD // Living cell come to die
                cell == Chess.State.DEAD && liveNeighbors == 3 ->
                    newChess[x, y] = Chess.State.LIVING // Dead cell come to live
            }
        }

        chess = newChess
    }

    operator fun get(x: Int, y: Int): Chess.State {
        return chess[x, y]
    }

    operator fun set(x: Int, y: Int, state: Chess.State) {
        chess[x, y] = state
    }

    fun put(x: Int, y: Int) {
        set(x, y, Chess.State.LIVING)
    }

    fun remove(x: Int, y: Int) {
        set(x, y, Chess.State.DEAD)
    }

    fun toggle(x: Int, y: Int) {
        set(
            x, y,
            if (get(x, y) == Chess.State.LIVING) Chess.State.DEAD
            else Chess.State.LIVING
        )
    }

    internal fun liveNeighbors(chess: Chess, x: Int, y: Int): Int {
        var liveNeighbors = 0
        if (chess[x - 1, y - 1] == Chess.State.LIVING) liveNeighbors++ // TopLeft
        if (chess[x, y - 1] == Chess.State.LIVING) liveNeighbors++ // Top
        if (chess[x + 1, y - 1] == Chess.State.LIVING) liveNeighbors++ // TopRight
        if (chess[x - 1, y] == Chess.State.LIVING) liveNeighbors++ // Left
        if (chess[x + 1, y] == Chess.State.LIVING) liveNeighbors++ // Right
        if (chess[x - 1, y + 1] == Chess.State.LIVING) liveNeighbors++ // BottomLeft
        if (chess[x, y + 1] == Chess.State.LIVING) liveNeighbors++ // Bottom
        if (chess[x + 1, y + 1] == Chess.State.LIVING) liveNeighbors++ // BottomRight
        return liveNeighbors
    }

    private fun ChessData.copy(): ChessData {
        val newChess = ChessData(height, width)
        this.forEachCell { x, y, cell ->
            newChess[x, y] = cell
        }
        return newChess
    }

    fun changeSize(width: Int, height: Int) {
        val newChess = ChessData(height, width)
        chess.forEachCell { x, y, cell ->
            newChess[x, y] = cell
        }
        chess = newChess
    }
}

val gosperGliderGun = """
000000000000000000000000000000000000
000000000000000000000000100000000000
000000000000000000000010100000000000
000000000000110000001100000000000011
000000000001000100001100000000000011
110000000010000010001100000000000000
110000000010001011000010100000000000
000000000010000010000000100000000000
000000000001000100000000000000000000
000000000000110000000000000000000000
"""

fun ChessController.putGosperGliderGun() {
    val initX = 20
    var x = initX
    var y = 20
    gosperGliderGun.forEach {
        when (it) {
            '0' -> this[x++, y] = Chess.State.DEAD
            '1' -> this[x++, y] = Chess.State.LIVING
            '\n' -> {
                y++
                x = initX
            }
        }
    }
}