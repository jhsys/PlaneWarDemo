package com.example.planewardemo.model

import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.InternalCoroutinesApi

enum class GameState {
    Waiting,
    Running,
    Paused,
    Dying,
    Over,
    Exit
}


@InternalCoroutinesApi
data class GameAction(
    val start: () -> Unit = {},
    val pause: () -> Unit = {},
    val reset: () -> Unit = {},
    val die: () -> Unit = {},
    val over: () -> Unit = {},
    val exit: () -> Unit = {},
    val playByRes: (resId: Int) -> Unit = { _: Int -> },
    val movePlayerPlane: (x: Int, y: Int) -> Unit = { _: Int, _: Int -> },
    val dragPlayerPlane: (dragAmount: Offset) -> Unit = { _: Offset -> },
    val createBullet: () -> Unit = { },
    val moveBullet: (bullet: Bullet) -> Unit = { _: Bullet -> },
    val moveEnemyPlane: (enemyPlane: EnemyPlane, onBombAnimChange: (Boolean) -> Unit) -> Unit ={ _: EnemyPlane, _: (Boolean) -> Unit -> },
    val moveAward: (award: Award) -> Unit = { _: Award -> },
    val destroyAllEnemy: () -> Unit = {},
)
