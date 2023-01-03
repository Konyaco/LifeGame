import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.awaitApplication
import androidx.compose.ui.window.rememberWindowState
import com.konyaco.fluent.FluentTheme
import com.konyaco.fluent.background.Layer
import com.konyaco.fluent.background.Mica
import com.konyaco.fluent.component.Button
import com.konyaco.fluent.component.Icon
import com.konyaco.fluent.component.Text
import com.konyaco.fluent.component.TextField
import com.konyaco.fluent.darkColors
import com.konyaco.fluent.icons.Icons
import com.konyaco.fluent.icons.filled.ArrowNext
import com.konyaco.fluent.icons.filled.Pause
import com.konyaco.fluent.icons.filled.Play
import com.konyaco.fluent.icons.filled.Stop
import com.konyaco.fluent.lightColors
import game.Chess
import game.forEachCell
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

suspend fun main() = awaitApplication {
    Window(
        onCloseRequest = ::exitApplication,
        icon = painterResource("icon.png"),
        title = "Game of Life",
        state = rememberWindowState(width = 800.dp, height = 672.dp)
    ) {
        val component = remember { Component() }
        val chess = component.chessData
        val state = component.state

        FluentTheme(
            if (isSystemInDarkTheme()) darkColors() else lightColors()
        ) {
            Mica(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxSize()) {
                    Chess(
                        modifier = Modifier.padding(16.dp).weight(1f).fillMaxHeight(),
                        chess = chess.value.chess,
                        onClick = { x, y ->
                            component.toggle(x, y)
                        }
                    )
                    // Control Panel
                    ControlPanel(state, component)
                }
            }
        }
    }
}

@Composable
private fun ControlPanel(state: State<Component.State>, component: Component) {
    Column(
        Modifier.padding(16.dp).fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        when (state.value) {
            Component.State.Stopped -> {
                Button(onClick = { component.play() }) {
                    Icon(Icons.Filled.Play, contentDescription = null)
                    Text("PLAY")
                }
            }

            Component.State.Running -> {
                Button(onClick = { component.pause() }) {
                    Icon(Icons.Filled.Pause, contentDescription = null)
                    Text("PAUSE")
                }
                Button(onClick = { component.stop() }) {
                    Icon(Icons.Filled.Stop, contentDescription = null)
                    Text("STOP")
                }
            }

            Component.State.Pausing -> {
                Button(onClick = { component.resume() }) {
                    Icon(Icons.Filled.Play, contentDescription = null)
                    Text("RESUME")
                }
                Button(onClick = { component.stop() }) {
                    Icon(Icons.Filled.Stop, contentDescription = null)
                    Text("STOP")
                }
            }
        }

        Button(onClick = { component.nextRound() }) {
            Icon(Icons.Filled.ArrowNext, contentDescription = null)
            Text("NEXT")
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            var speed by remember { mutableStateOf(TextFieldValue("20")) }

            TextField(
                modifier = Modifier.width(120.dp),
                value = speed,
                onValueChange = {
                    if (it.text.all { it.isDigit() }) {
                        speed = it
                    }
                },
                header = { Text("Speed") }
            )

            Button(onClick = { component.setSpeed(speed.text.toInt()) }) {
                Text("APPLY")
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            var height by remember { mutableStateOf(TextFieldValue("100")) }
            TextField(
                modifier = Modifier.width(120.dp),
                value = height,
                onValueChange = {
                    if (it.text.all { it.isDigit() }) {
                        height = it
                    }
                },
                header = { Text("Height") }
            )
            var width by remember { mutableStateOf(TextFieldValue("100")) }
            TextField(
                modifier = Modifier.width(120.dp),
                value = width,
                onValueChange = {
                    if (it.text.all { it.isDigit() }) {
                        width = it
                    }
                },
                header = {
                    Text("Width")
                }
            )

            Button(onClick = { component.changeSize(width.text.toInt(), height.text.toInt()) }) {
                Text("APPLY")
            }
        }
    }
}


val livingColor = Brush.linearGradient(listOf(Color.White, Color.LightGray))
val deadColor = SolidColor(Color.DarkGray)

@Composable
fun Chess(chess: Chess, onClick: (x: Int, y: Int) -> Unit, modifier: Modifier) {
    Layer(modifier, shape = RoundedCornerShape(4.dp), elevation = 2.dp) {
        BoxWithConstraints {
            val length by animateFloatAsState(
                calculateDotLength(
                    constraints.maxWidth,
                    constraints.maxHeight,
                    chess.height,
                    chess.width
                )
            )

            Canvas(Modifier.fillMaxSize().clipToBounds().pointerInput(Unit) {
                forEachGesture {
                    detectTapGestures {
                        val x = (it.x / length).toInt()
                        val y = (it.y / length).toInt()
                        onClick(x, y)
                    }
                }
            }) {
                chess.forEachCell { x, y, cell ->
                    val brush = if (cell == Chess.State.LIVING) livingColor else deadColor
                    drawRect(
                        topLeft = Offset(length * x, length * y),
                        size = Size(length, length),
                        brush = brush
                    )
                }
            }
        }
    }
}

@Stable
private fun calculateDotLength(
    maxWidth: Int,
    maxHeight: Int,
    chessHeight: Int,
    chessWidth: Int
) = (min(maxWidth, maxHeight).toFloat() /
        max(chessHeight, chessWidth).toFloat())
    .roundToInt().toFloat()