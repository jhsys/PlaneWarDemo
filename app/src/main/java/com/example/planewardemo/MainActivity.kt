package com.example.planewardemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.planewardemo.model.GameState
import com.example.planewardemo.ui.theme.ComposePlaneTheme
import com.example.planewardemo.util.LogUtil
import com.example.planewardemo.util.StatusBarUtil
import com.example.planewardemo.view.Stage
import com.example.planewardemo.viewmodel.GameViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @InternalCoroutinesApi
    private val gameViewModel: GameViewModel by viewModels()

    @InternalCoroutinesApi
    @ExperimentalComposeUiApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        StatusBarUtil.transparentStatusBar(this)
        lifecycle.addObserver(GameLifecycleObserver(gameViewModel))

        lifecycleScope.launch {
            gameViewModel.gameStateFlow.collect {
                LogUtil.printLog(message = "lifecycleScope gameState $it")
                if (GameState.Waiting == it) {
                    gameViewModel.onGameInit()
                }
                if (GameState.Exit == it) {
                    finish()
                }
            }
        }

        //开启启动游戏界面
        setContent {
            ComposePlaneTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Stage(gameViewModel)
                }
            }
        }
    }

}