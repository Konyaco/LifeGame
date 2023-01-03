package game

internal class ChessData(
    height: Int = 100,
    width: Int = 100
) : Chess {
    override var height: Int = height
        private set
    override var width: Int = width
        private set

    private var chessBoard = newArray()

    private fun newArray(): Array<BooleanArray> {
        return Array(height) { BooleanArray(width) { false } }
    }

    override operator fun get(x: Int, y: Int): Chess.State {
        return if(chessBoard.getOrNull(y)?.getOrNull(x) == true) Chess.State.LIVING else Chess.State.DEAD
    }

    operator fun set(x: Int, y: Int, state: Chess.State) {
        if (y < height && x < width) {
            chessBoard[y][x] = state == Chess.State.LIVING
        }
    }

    override fun toString(): String {
        return chessBoard.joinToString("\n") {
            it.joinToString("") { if (it) "+" else " " }
        }
    }
}