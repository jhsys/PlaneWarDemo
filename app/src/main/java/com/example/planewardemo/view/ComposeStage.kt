package com.example.planewardemo.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planewardemo.model.GameAction
import com.example.planewardemo.viewmodel.GameViewModel
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun Stage(gameViewModel: GameViewModel) {

    val gameState by gameViewModel.gameStateFlow.collectAsState()

    val gameScore by gameViewModel.gameScoreStateFlow.collectAsState(0)

    val playerPlane by gameViewModel.playerPlaneStateFlow.collectAsState()

    val bulletList by gameViewModel.bulletListStateFlow.collectAsState()

    val awardList by gameViewModel.awardListStateFlow.collectAsState()

    val enemyPlaneList by gameViewModel.enemyPlaneListStateFlow.collectAsState()

    val gameAction: GameAction = gameViewModel.onGameAction

    val modifier = Modifier.fillMaxSize()

    Box(modifier = modifier) {

        FarBackground(modifier)

        GameStart(gameState, playerPlane, gameAction)

        PlayerPlaneSprite(
            gameState,
            playerPlane,
            gameAction
        )

        PlayerPlaneAnimIn(
            gameState,
            playerPlane,
            gameAction
        )

        PlayerPlaneBombSprite(gameState, playerPlane, gameAction)

        EnemyPlaneSprite(
            gameState,
            gameScore,
            enemyPlaneList,
            gameAction
        )

        BulletSprite(gameState, bulletList, gameAction)

        AwardSprite(gameState, awardList, gameAction)

        BombAward(playerPlane, gameAction)

        GameScore(gameState, gameScore, gameAction)

        GameOver(gameState, gameScore, gameAction)

    }

}

@InternalCoroutinesApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Preview()
@Composable
fun PreviewStage() {
    val gameViewModel: GameViewModel = viewModel()
    Stage(gameViewModel)
}
