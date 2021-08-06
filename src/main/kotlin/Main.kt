import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.awaitApplication
import game.Chess
import game.forEachCell
import kotlinx.coroutines.withContext
import kotlin.math.round

suspend fun main() = awaitApplication {
    Window(
        visible = true,
        onCloseRequest = ::exitApplication,
        icon = painterResource("icon.png"),
        title = "Life Game"
    ) {
        val component = remember { Component() }
        val chess = component.chessFlow.collectAsState()
        val state = component.state.collectAsState()

        MaterialTheme {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                Row(Modifier.fillMaxSize()) {
                    Chess(
                        modifier = Modifier.padding(16.dp).weight(1f).fillMaxHeight(),
                        chess = chess.value.chess,
                        onClick = { x, y ->
                            component.toggle(x, y)
                        }
                    )
                    // Control Panel
                    Column(
                        Modifier.padding(16.dp).wrapContentWidth().fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(Modifier.wrapContentHeight(), Arrangement.spacedBy(8.dp)) {
                            when (state.value) {
                                Component.State.Stopped -> {
                                    Button(onClick = { component.play() }) { Text("PLAY") }
                                }
                                Component.State.Running -> {
                                    Button(onClick = { component.pause() }) { Text("PAUSE") }
                                    Button(onClick = { component.stop() }) { Text("STOP") }
                                }
                                Component.State.Pausing -> {
                                    Button(onClick = { component.resume() }) { Text("RESUME") }
                                    Button(onClick = { component.stop() }) { Text("STOP") }
                                }
                            }
                        }
                        Button(onClick = { component.nextRound() }) { Text("NEXT ROUND") }
                        Row(
                            modifier = Modifier.wrapContentWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            var speed by remember { mutableStateOf(4) }
                            TextField(
                                modifier = Modifier.width(120.dp),
                                value = speed.toString(),
                                onValueChange = { it.toIntOrNull()?.let { speed = it } },
                                label = { Text("Speed") })
                            Button(onClick = { component.setSpeed(speed) }) {
                                Text("APPLY")
                            }
                        }
                        Row(
                            modifier = Modifier.wrapContentWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            var height by remember { mutableStateOf(100) }
                            var width by remember { mutableStateOf(100) }
                            TextField(
                                modifier = Modifier.width(120.dp),
                                value = height.toString(),
                                onValueChange = { it.toIntOrNull()?.let { height = it } },
                                label = { Text("Height") }
                            )
                            TextField(
                                modifier = Modifier.width(120.dp),
                                value = width.toString(),
                                onValueChange = { it.toIntOrNull()?.let { width = it } },
                                label = { Text("Width") }
                            )
                            Button(onClick = { component.changeSize(width, height) }) {
                                Text("APPLY")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Chess(chess: Chess, onClick: (x: Int, y: Int) -> Unit, modifier: Modifier) {
    Surface(modifier, shape = RoundedCornerShape(4.dp), elevation = 2.dp) {
        BoxWithConstraints {
            var length by remember { mutableStateOf(0f) }
            LaunchedEffect(constraints, chess.height) {
                length = round(constraints.maxHeight.toFloat() / chess.height)
            }
            Canvas(Modifier.fillMaxSize().clipToBounds().pointerInput(Unit) {
                detectTapGestures {
                    val x = (it.x / length).toInt()
                    val y = (it.y / length).toInt()
                    onClick(x, y)
                }
            }) {
                chess.forEachCell { x, y, cell ->
                    val brush = if (cell == Chess.State.LIVING) Brush.linearGradient(
                        colors = listOf(
                            Color.White,
                            Color.LightGray
                        )
                    ) else SolidColor(Color.DarkGray)
                    drawRect(topLeft = Offset(length * x, length * y), size = Size(length, length), brush = brush)
                }
            }
        }
    }
}