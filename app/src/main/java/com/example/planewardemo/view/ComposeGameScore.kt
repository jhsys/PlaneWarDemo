package com.example.planewardemo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planewardemo.R
import com.example.planewardemo.model.GameAction
import com.example.planewardemo.model.GameState
import com.example.planewardemo.util.LogUtil
import com.example.planewardemo.util.ScoreFontFamily
import kotlinx.coroutines.InternalCoroutinesApi


//计分牌
@InternalCoroutinesApi
@Composable
fun GameScore(
    gameState: GameState = GameState.Paused,
    gameScore: Int = 0,
    gameAction: GameAction = GameAction()
) {
    LogUtil.printLog(message = "GameScore()")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp)
            .absolutePadding(top = 60.dp)
            .alpha(if (gameState == GameState.Running || gameState == GameState.Paused) 1f else 0f)
    ) {

        Image(
            painter = painterResource(
                id = if (gameState == GameState.Running) {
                    R.drawable.pause
                } else {
                    R.drawable.play
                }
            ),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(40.dp)
                .clickable(
                    onClick = if (gameState == GameState.Running) {
                        gameAction.pause
                    } else {
                        gameAction.start
                    }
                )
        )

        Spacer(
            modifier = Modifier
                .weight(2f)
        )

        Text(
            text = "score: $gameScore",
            modifier = Modifier
                .padding(start = 4.dp)
                .align(Alignment.CenterVertically)
                .wrapContentWidth(Alignment.End),
            style = MaterialTheme.typography.h5,
            color = Color.Green,
            fontFamily = ScoreFontFamily
        )

    }
}

@InternalCoroutinesApi
@Preview()
@Composable
fun PreviewComposeScore() {
    GameScore()
}
