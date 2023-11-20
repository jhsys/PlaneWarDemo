package com.example.planewardemo.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.example.planewardemo.R
import com.example.planewardemo.model.GameAction
import com.example.planewardemo.model.GameState
import com.example.planewardemo.model.PlayerPlane
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.math.roundToInt


val FastShowAndHiddenEasing: Easing = CubicBezierEasing(0.0f, 0.0f, 1.0f, 1.0f)
const val SMALL_ENEMY_PLANE_SPRITE_ALPHA = 100;

@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun PlayerPlaneSprite(
    gameState: GameState,
    playerPlane: PlayerPlane,
    gameAction: GameAction
) {
    if (!(gameState == GameState.Running || gameState == GameState.Paused)) {
        return
    }

    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(SMALL_ENEMY_PLANE_SPRITE_ALPHA, easing = FastShowAndHiddenEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    if (gameState == GameState.Running && !playerPlane.isNoProtect() && alpha >= 0.5f) {
        playerPlane.reduceProtect()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.player_plane_1),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(playerPlane.x, playerPlane.y) }
                .size(playerPlane.width, playerPlane.height)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consumeAllChanges()
                        gameAction.dragPlayerPlane(dragAmount)
                    }
                }
                .alpha(
                    if (gameState == GameState.Running || gameState == GameState.Paused) {
                        if (alpha < 0.5f) 0f else 1f
                    } else {
                        0f
                    }
                )
        )

        Image(
            painter = painterResource(id = R.drawable.player_plane_2),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(playerPlane.x, playerPlane.y) }
                .size(playerPlane.width, playerPlane.height)
                .alpha(
                    if (gameState == GameState.Running || gameState == GameState.Paused) {
                        if (!playerPlane.isNoProtect()) {
                            0f
                        } else {
                            if (1 - alpha < 0.5f) 0f else 1f
                        }
                    } else {
                        0f
                    }
                )
        )
    }
}

@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun PlayerPlaneAnimIn(
    gameState: GameState,
    playerPlane: PlayerPlane,
    gameAction: GameAction = GameAction()
) {
    if (gameState != GameState.Running) {
        return
    }


    val heightPixels = LocalContext.current.resources.displayMetrics.heightPixels
    val playerPlaneHeightPx = with(LocalDensity.current) { playerPlane.height.toPx() }
    val startOffsetY = playerPlane.startY.toFloat()
    val endOffsetY = heightPixels - playerPlaneHeightPx * 1.5f
    val realOffsetX by remember { mutableStateOf(playerPlane.startX.toFloat()) }
    var realOffsetY by remember { mutableStateOf(playerPlane.startY.toFloat()) }

    var animInState by remember { mutableStateOf(false) }
    var show by remember { mutableStateOf(false) }
    var offsetYIn by remember {
        mutableStateOf(startOffsetY)
    }
    var playTimeIn by remember { mutableStateOf(0L) }
    val animIn = remember {
        TargetBasedAnimation(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = 0,
                easing = LinearOutSlowInEasing
            ),
            typeConverter = Float.VectorConverter,
            initialValue = startOffsetY,
            targetValue = endOffsetY
        )
    }
    LaunchedEffect(animInState) {
        val startTime = withFrameNanos { it }
        do {
            playTimeIn = withFrameNanos { it } - startTime
            offsetYIn = animIn.getValueFromNanos(playTimeIn)
        } while (!animIn.isFinishedFromNanos(playTimeIn))

    }

    if (!playerPlane.animateIn) {
        return
    }

    if (gameState == GameState.Running && !show && !animInState) {
        offsetYIn = startOffsetY
        show = true
        animInState = true
    }

    if (gameState == GameState.Dying && !show) {
        offsetYIn = startOffsetY
        realOffsetY = startOffsetY
        animInState = false
    }

    if (show) {
        realOffsetY = offsetYIn
    }

    if (show && offsetYIn <= endOffsetY) {
        show = false
        gameAction.movePlayerPlane(realOffsetX.roundToInt(), realOffsetY.roundToInt())
    }

    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(SMALL_ENEMY_PLANE_SPRITE_ALPHA, easing = FastShowAndHiddenEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.player_plane_1),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(realOffsetX.roundToInt(), realOffsetY.roundToInt()) }
                .size(playerPlane.width, playerPlane.height)
                .alpha(
                    if (show) {
                        if (alpha < 0.5f) 0f else 1f
                    } else {
                        0f
                    }
                )
        )

        Image(
            painter = painterResource(id = R.drawable.player_plane_2),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(realOffsetX.roundToInt(), realOffsetY.roundToInt()) }
                .size(playerPlane.width, playerPlane.height)
                .alpha(
                    if (show) {
                        if (1 - alpha < 0.5f) 0f else 1f
                    } else {
                        0f
                    }
                )
        )
    }
}


@InternalCoroutinesApi
@ExperimentalAnimationApi
@Preview()
@Composable
fun PreviewFighterJetPlaneSprite() {
    PlayerPlaneSprite(
        GameState.Waiting,
        PlayerPlane(x = 480, y = 1900),
        GameAction()
    )
}
