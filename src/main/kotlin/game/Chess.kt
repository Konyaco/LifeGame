package game

interface Chess {
    val height: Int
    val width: Int

    enum class State {
        DEAD, LIVING
    }

    operator fun get(x: Int, y: Int): State
}

inline fun Chess.forEachCell(block: (x: Int, y: Int, cell: Chess.State) -> Unit) {
    for (x in 0 until width) {
        for (y in 0 until height) {
            block(x, y, this[x, y])
        }
    }
}