package com.example.planewardemo.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.example.planewardemo.model.EnemyPlane
import com.example.planewardemo.model.GameAction
import com.example.planewardemo.model.GameState
import com.example.planewardemo.util.LogUtil
import kotlinx.coroutines.InternalCoroutinesApi



@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun EnemyPlaneSprite(
    gameState: GameState,
    gameScore: Int,
    enemyPlaneList: List<EnemyPlane>,
    gameAction: GameAction
) {
    for (enemyPlane in enemyPlaneList) {
        EnemyPlaneSpriteBombAndFly(
            gameState,
            gameScore,
            enemyPlane,
            gameAction
        )
    }
}

@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun EnemyPlaneSpriteBombAndFly(
    gameState: GameState,
    gameScore: Int,
    enemyPlane: EnemyPlane,
    gameAction: GameAction
) {
    var showBombAnim by remember {
        mutableStateOf(false)
    }

    EnemyPlaneSpriteMove(
        gameState,
        onBombAnimChange = {
            showBombAnim = it
        },
        enemyPlane,
        gameAction
    )

    EnemyPlaneSpriteBomb(gameScore, enemyPlane, showBombAnim,
        onBombAnimChange = {
            showBombAnim = it
        })

}


@InternalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun EnemyPlaneSpriteMove(
    gameState: GameState,
    onBombAnimChange: (Boolean) -> Unit,
    enemyPlane: EnemyPlane,
    gameAction: GameAction
) {
    val infiniteTransition = rememberInfiniteTransition()
    val frame by infiniteTransition.animateInt(
        initialValue = 0,
        targetValue = 60,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    if (gameState != GameState.Running) {
        return
    }

    gameAction.moveEnemyPlane(enemyPlane,onBombAnimChange)

    LogUtil.printLog(message = "EnemyPlaneSpriteFly: state = ${enemyPlane.state}，enemyPlane.x = ${enemyPlane.x}， enemyPlane.y = ${enemyPlane.y}, frame = $frame ")

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(enemyPlane.getRealDrawableId()),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(enemyPlane.x, enemyPlane.y) }
                .size(enemyPlane.width)
                .alpha(if (enemyPlane.isAlive()) 1f else 0f)
        )
    }

}

@InternalCoroutinesApi
@ExperimentalAnimationApi
@Preview()
@Composable
fun PreviewEnemyPlaneSprite() {

}
