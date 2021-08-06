package game

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TimeController(
    speed: Int = 5,
    val chessController: ChessController,
    private val onUpdate: () -> Unit = {}
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    var speed: Int = speed
        private set

    private val mutex = Mutex()
    var started: Boolean = false
        private set

    private var task: Job? = null

    private fun newTask(): Job {
        return scope.launch {
            while (isActive) {
                if (started) {
                    chessController.nextRound()
                    onUpdate()
                }
                delay(1000L / speed)
            }
        }
    }

    fun start() {
        scope.launch {
            mutex.withLock {
                started = true
            }
            task?.cancelAndJoin()
            task = newTask()
        }
        println("Update")
    }

    fun resume() {
        scope.launch {
            mutex.withLock {
                started = true
            }
        }
    }

    fun pause() {
        scope.launch {
            mutex.withLock {
                started = false
            }
        }
    }

    fun stop() {
        scope.launch {
            mutex.withLock {
                started = false
                chessController.newChess()
            }
        }
    }

    fun changeSpeed(speed: Int) {
        this.speed = speed
    }
}
