package com.example.planewardemo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planewardemo.model.GameState
import com.example.planewardemo.model.GameAction
import com.example.planewardemo.util.LogUtil
import com.example.planewardemo.util.ScoreFontFamily
import kotlinx.coroutines.InternalCoroutinesApi


//游戏结束背景
@InternalCoroutinesApi
@Composable
fun GameOver(
    gameState: GameState,
    gameScore: Int,
    gameAction: GameAction = GameAction()
) {
    LogUtil.printLog(message = "GameOverBoard()")
    Box(
        modifier = Modifier
            .wrapContentSize()
            .alpha(if (gameState == GameState.Over) 1.0f else 0f)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {

            Spacer(
                modifier = Modifier
                    .weight(2f)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
            ) {


                Spacer(
                    modifier = Modifier
                        .size(30.dp)
                )

                Text(
                    text = "score: $gameScore",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                        .align(Alignment.CenterHorizontally)
                        .wrapContentWidth(Alignment.End),
                    style = MaterialTheme.typography.h5,
                    color = Color.Green,
                    fontFamily = ScoreFontFamily
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentSize()
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Transparent),
                ) {

                    Spacer(
                        modifier = Modifier
                            .size(40.dp)
                    )

                    TextButton(
                        onClick = gameAction.reset,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentSize()
                            .align(Alignment.CenterVertically)
                            .background(Color.Transparent),
                        content = {
                            Text(
                                text = "重新开始",
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .wrapContentWidth(Alignment.End),
                                style = MaterialTheme.typography.h5,
                                color = Color.Green,
                                fontFamily = ScoreFontFamily
                            )
                        }
                    )

                    TextButton(
                        onClick = gameAction.exit,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentSize()
                            .align(Alignment.CenterVertically)
                            .background(Color.Transparent),
                        content = {
                            Text(
                                text = "退出游戏",
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
                        modifier = Modifier
                            .size(40.dp)
                    )
                }

                Spacer(
                    modifier = Modifier
                        .size(30.dp)
                )
            }

            Spacer(
                modifier = Modifier
                    .weight(2f)
            )

        }

    }
}

@InternalCoroutinesApi
@Preview()
@Composable
fun PreviewGameOver() {
    FarBackground()
    GameOver(GameState.Over, 100, GameAction())
}

