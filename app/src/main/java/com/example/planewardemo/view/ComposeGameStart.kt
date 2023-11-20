package com.example.planewardemo.view


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planewardemo.model.GameAction
import com.example.planewardemo.model.GameState
import com.example.planewardemo.model.PLAYER_PLANE_SPRITE_SIZE
import com.example.planewardemo.model.PlayerPlane
import com.example.planewardemo.util.LogUtil
import com.example.planewardemo.util.ScoreFontFamily
import kotlinx.coroutines.InternalCoroutinesApi


@ExperimentalAnimationApi
@InternalCoroutinesApi
@Composable
fun GameStart(
    gameState: GameState,
    playerPlane: PlayerPlane,
    gameAction: GameAction = GameAction()

) {
    LogUtil.printLog(message = "GameStart()")

    val widthPixels = LocalContext.current.resources.displayMetrics.widthPixels
    val heightPixels = LocalContext.current.resources.displayMetrics.heightPixels

    Box(
        modifier = Modifier
            .wrapContentSize()
            .alpha(if (gameState == GameState.Waiting) 1.0f else 0f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Spacer(
                Modifier
                    .weight(1f)
            )

            Spacer(
                Modifier
                    .weight(1f)
            )

            TextButton(
                onClick = gameAction.start,
                modifier = Modifier
                    .weight(3f)
                    .padding(20.dp)
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally)
                    .background(Color.Transparent),
                content = {
                    Text(
                        text = "开始游戏START",
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .wrapContentWidth(Alignment.End),
                        style = MaterialTheme.typography.h5,
                        color = Color.Green,
                        fontFamily = ScoreFontFamily
                    )
                }
            )

            Spacer(
                Modifier
                    .weight(1f)
            )
        }

    }

    GameStartPlaneInAndOut(gameState, playerPlane, widthPixels, heightPixels)

}


@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun GameStartPlaneInAndOut(
    gameState: GameState,
    playerPlane: PlayerPlane,
    widthPixels: Int,
    heightPixels: Int
) {
    if (!playerPlane.animateIn) {
        return
    }

    val playerPlaneSize = PLAYER_PLANE_SPRITE_SIZE.dp
    val playerPlaneSizePx = with(LocalDensity.current) { playerPlaneSize.toPx() }

    val startOffsetYIn = heightPixels + playerPlaneSizePx
    val endOffsetYIn = heightPixels / 2f - playerPlaneSizePx / 2f

    var offsetYIn by remember {
        mutableStateOf(startOffsetYIn)
    }

    var realOffsetY by remember {
        mutableStateOf(0f)
    }

    val realOffsetX by remember {
        mutableStateOf(widthPixels / 2f - playerPlaneSizePx / 2f)
    }

    var show by remember {
        mutableStateOf(true)
    }

    if (gameState == GameState.Over) {
        show = false
    }

    val animateInState by remember { mutableStateOf(0) }
    var playTimeIn by remember { mutableStateOf(0L) }
    val animIn = remember {
        TargetBasedAnimation(
            animationSpec = tween(
                durationMillis = 500,
                delayMillis = 200,
                easing = FastOutSlowInEasing
            ),
            typeConverter = Float.VectorConverter,
            initialValue = startOffsetYIn,
            targetValue = endOffsetYIn
        )
    }
    LaunchedEffect(animateInState) {
        val startTime = withFrameNanos { it }
        do {
            playTimeIn = withFrameNanos { it } - startTime
            offsetYIn = animIn.getValueFromNanos(playTimeIn)
        } while (!animIn.isFinishedFromNanos(playTimeIn))

    }

    var offsetYOut by remember {
        mutableStateOf(endOffsetYIn)
    }
    var playTimeOut by remember { mutableStateOf(0L) }
    val animOut = remember {
        TargetBasedAnimation(
            animationSpec = tween(
                durationMillis = 200,
                delayMillis = 0,
                easing = LinearEasing
            ),
            typeConverter = Float.VectorConverter,
            initialValue = endOffsetYIn,
            targetValue = -playerPlaneSizePx * 2f
        )
    }
    LaunchedEffect(gameState) {
        val startTime = withFrameNanos { it }
        do {
            playTimeOut = withFrameNanos { it } - startTime
            offsetYOut = animOut.getValueFromNanos(playTimeOut)
        } while (!animOut.isFinishedFromNanos(playTimeOut))

    }

    if (gameState == GameState.Waiting) {
        realOffsetY = offsetYIn
    }
    if (gameState == GameState.Running) {
        realOffsetY = offsetYOut
    }
}


@InternalCoroutinesApi
@ExperimentalAnimationApi
@Preview()
@Composable
fun PreviewGameStart() {
    FarBackground()

    val widthPixels = LocalContext.current.resources.displayMetrics.widthPixels
    val playerPlaneSize = PLAYER_PLANE_SPRITE_SIZE.dp
    val playerPlaneSizePx = with(LocalDensity.current) { playerPlaneSize.toPx() }
    val offsetX = widthPixels / 2f - playerPlaneSizePx / 2f
    val offsetY = widthPixels / 2f - playerPlaneSizePx / 2f

    GameStart(
        GameState.Waiting,
        PlayerPlane(x = offsetX.toInt(), y = offsetY.toInt()),
        GameAction()
    )
}

