import androidx.compose.runtime.mutableStateOf
import game.*

class Component {
    private val chessController = ChessController().apply {
        putGosperGliderGun()
    }
    private val timeController = TimeController(20, chessController, onUpdate = { update() })

    val chessData = mutableStateOf(ChessData(chessController.getChess(), System.currentTimeMillis()))

    val state = mutableStateOf<State>(State.Stopped)

    sealed class State {
        object Running : State()
        object Pausing : State()
        object Stopped : State()
    }

    data class ChessData(val chess: Chess, val time: Long)

    private fun update() {
        chessData.value = ChessData(chessController.getChess(), System.currentTimeMillis())
    }

    fun put(x: Int, y: Int) {
        chessController.set(x, y, Chess.State.LIVING)
        update()
    }

    fun remove(x: Int, y: Int) {
        chessController.set(x, y, Chess.State.DEAD)
        update()
    }

    fun toggle(x: Int, y: Int) {
        chessController.toggle(x, y)
        update()
    }

    fun changeSize(width: Int, height: Int) {
        chessController.changeSize(width, height)
        update()
    }

    fun setSpeed(speed: Int) {
        timeController.changeSpeed(speed)
    }

    fun play() {
        timeController.start()
        state.value = State.Running
    }

    fun pause() {
        timeController.pause()
        state.value = State.Pausing
    }

    fun resume() {
        timeController.resume()
        state.value = State.Running
    }

    fun stop() {
        timeController.stop()
        update()
        state.value = State.Stopped
    }

    fun clear() {
        chessController.newChess()
        update()
    }

    fun nextRound() {
        chessController.nextRound()
        update()
    }
}
