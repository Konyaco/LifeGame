package game

class ChessController {
    @Volatile
    private var chess: ChessImpl = ChessImpl()

    fun getChess(): Chess {
        return chess
    }

    fun newChess() {
        chess = ChessImpl(chess.height, chess.width)
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

    fun get(x: Int, y: Int): Chess.State {
        return chess[x, y]
    }

    fun set(x: Int, y: Int, state: Chess.State) {
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

    private fun ChessImpl.copy(): ChessImpl {
        val newChess = ChessImpl(height, width)
        this.forEachCell { x, y, cell ->
            newChess[x, y] = cell
        }
        return newChess
    }

    fun changeSize(width: Int, height: Int) {
        val newChess = ChessImpl(height, width)
        chess.forEachCell { x, y, cell ->
            newChess[x, y] = cell
        }
        chess = newChess
    }
}