package com.example.planewardemo.view

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.planewardemo.R
import com.example.planewardemo.model.*
import com.example.planewardemo.util.LogUtil
import com.example.planewardemo.util.ScoreFontFamily
import kotlinx.coroutines.InternalCoroutinesApi

@SuppressLint("RememberReturnType")
@InternalCoroutinesApi
@Composable
fun AwardSprite(
    gameState: GameState,
    awardList: List<Award>,
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


    for ((index, award) in awardList.withIndex()) {
        if (award.isAlive()) {
            AwardSpriteFall(gameState, award, index, gameAction)
        }
    }

    LogUtil.printLog(message = "ComposeAwardSprite()---> frame = $frame, awardList.size = ${awardList.size}")

}


/**
 * 奖励精灵下落
 */
@InternalCoroutinesApi
@Composable
fun AwardSpriteFall(
    gameState: GameState = GameState.Waiting,
    award: Award,
    index: Int,
    gameAction: GameAction = GameAction()

) {
    LogUtil.printLog(message = "ComposeAwardSpriteFall() ---> award = $award")

    if (gameState != GameState.Running) {
        return
    }

    gameAction.moveAward(award)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = award.drawableId),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .offset {
                    IntOffset(
                        award.x,
                        award.y
                    )
                }
                .width(award.width)
                .height(award.height)
                .alpha(
                    if (gameState == GameState.Running) {
                        if (award.isDead()) 0f else 1f
                    } else {
                        0f
                    }
                )
        )
    }

}

@InternalCoroutinesApi
@Preview()
@Composable
fun PreviewComposeAward() {

}


@InternalCoroutinesApi
@Composable
fun BombAward(
    playerPlane: PlayerPlane = PlayerPlane(bombAward = (0 shl 16 or 100)),
    gameAction: GameAction = GameAction()
) {
    LogUtil.printLog(message = "ComposeBombAward()")
    val bombAward = playerPlane.bombAward
    val bombNum = bombAward and 0xFFFF
    val bombWidth = 44.dp
    val bombHeight = 40.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
                .alpha(if (bombNum > 0) 1f else 0f)
        ) {
            Spacer(
                modifier = Modifier
                    .weight(1f)
            )
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .wrapContentSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.small_enemy_plane2),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(bombWidth, bombHeight)
                        .clickable(onClick = gameAction.destroyAllEnemy)
                )

                Text(
                    text = " x $bombNum",
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically)
                        .wrapContentWidth(Alignment.End),
                    style = MaterialTheme.typography.h4,
                    color = Color.Black,
                    fontFamily = ScoreFontFamily
                )

            }
        }

    }

}

@InternalCoroutinesApi
@Preview()
@Composable
fun PreviewComposeBombAward() {
    BombAward()
}
