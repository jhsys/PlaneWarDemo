package com.example.planewardemo.view


import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.example.planewardemo.model.Bullet
import com.example.planewardemo.model.GameAction
import com.example.planewardemo.model.GameState
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi
@Composable
fun BulletSprite(
    gameState: GameState = GameState.Waiting,
    bulletList: List<Bullet> = mutableListOf(),
    gameAction: GameAction = GameAction()
) {
    val infiniteTransition = rememberInfiniteTransition()
    val frame by infiniteTransition.animateInt(
        initialValue = 0,
        targetValue = 60,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    if (gameState != GameState.Running) {
        return
    }

    if (frame % 6 == 0) {
        gameAction.createBullet()
    }

    for (bullet in bulletList) {

        if (bullet.isAlive()) {

            gameAction.moveBullet(bullet)

            BulletShootingSprite(bullet)
        }

    }

}


@InternalCoroutinesApi
@Composable
fun BulletShootingSprite(
    bullet: Bullet = Bullet()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bullet.drawableId),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset {
                    IntOffset(
                        bullet.x,
                        bullet.y
                    )
                }
                .width(bullet.width)
                .height(bullet.height)
                .alpha(
                    if (bullet.isAlive()) {
                        1f
                    } else {
                        0f
                    }
                )
        )
    }
}

@Preview()
@Composable
fun PreviewBulletSprite() {

}

