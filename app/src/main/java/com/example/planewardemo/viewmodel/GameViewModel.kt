package com.example.planewardemo.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.planewardemo.R
import com.example.planewardemo.model.*
import com.example.planewardemo.util.DensityUtil
import com.example.planewardemo.util.LogUtil
import com.example.planewardemo.util.SpriteUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.roundToInt

@InternalCoroutinesApi
class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val id = AtomicLong(0L)

    private val _gameStateFlow = MutableStateFlow(GameState.Waiting)

    val gameStateFlow = _gameStateFlow.asStateFlow()

    private fun onGameStateFlowChange(newGameSate: GameState) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _gameStateFlow.emit(newGameSate)
            }
        }
    }


    private val _playerPlaneStateFlow = MutableStateFlow(PlayerPlane())

    val playerPlaneStateFlow = _playerPlaneStateFlow.asStateFlow()

    private fun onPlayerPlaneStateFlowChange(plane: PlayerPlane) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _playerPlaneStateFlow.emit(plane)
            }
        }
    }


    private val _enemyPlaneListStateFlow = MutableStateFlow(mutableListOf<EnemyPlane>())

    val enemyPlaneListStateFlow = _enemyPlaneListStateFlow.asStateFlow()

    private fun onEnemyPlaneListStateFlowChange(list: MutableList<EnemyPlane>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _enemyPlaneListStateFlow.emit(list)
            }
        }
    }

    private val _bulletListStateFlow = MutableStateFlow(CopyOnWriteArrayList<Bullet>())

    val bulletListStateFlow = _bulletListStateFlow.asStateFlow()

    private fun onBulletListStateFlowChange(list: CopyOnWriteArrayList<Bullet>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _bulletListStateFlow.emit(list)
            }
        }
    }

    private val _awardListStateFlow = MutableStateFlow(CopyOnWriteArrayList<Award>())

    val awardListStateFlow = _awardListStateFlow.asStateFlow()

    private fun onAwardListStateFlowChange(list: CopyOnWriteArrayList<Award>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _awardListStateFlow.emit(list)
            }
        }
    }


    private val _gameScoreStateFlow = MutableStateFlow(0)
    val gameScoreStateFlow = _gameScoreStateFlow.asStateFlow()

    private fun onGameScoreStateFlowChange(score: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _gameScoreStateFlow.emit(score)
            }
        }
    }


    private val _gameLevelStateFlow = MutableStateFlow(0)
    private val gameLevelStateFlow = _gameLevelStateFlow

    private fun onGameLevelStateFlowChange(level: Int) {
        if (_gameLevelStateFlow.value != level) {
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    _gameLevelStateFlow.emit(level)
                }
            }
            when (level) {
                1 -> createEnemySprite(3, 2, 1)
                2 -> createEnemySprite(6, 3, 2)
                3 -> createEnemySprite(10, 5, 3)
            }
        }
    }


    init {
        viewModelScope.launch {

            gameStateFlow.collect {
                LogUtil.printLog(message = "viewModelScope gameState $it")
            }

            gameLevelStateFlow.collect {
                LogUtil.printLog(message = "viewModelScope gameLevelStateFlow $it")
            }
        }

    }


    fun onGameInit() {

        initPlayerSprite()
        onGameLevelStateFlowChange(1)
    }


    private fun initPlayerSprite() {
        val resources = getApplication<Application>().resources
        val dm = resources.displayMetrics
        val widthPixels = dm.widthPixels
        val heightPixels = dm.heightPixels

        val playerPlaneSizePx = DensityUtil.dp2px(resources, PLAYER_PLANE_SPRITE_SIZE)
        val startX = widthPixels / 2 - playerPlaneSizePx!! / 2
        val startY = (heightPixels * 1.5).toInt()
        val playerPlane = playerPlaneStateFlow.value
        playerPlane.startX = startX
        playerPlane.startY = startY
        playerPlane.reBirth()
        LogUtil.printLog(message = "initPlayerSprite playerPlane $playerPlane")
        onPlayerPlaneStateFlowChange(playerPlane)
    }

    private fun onPlayerPlaneMove(x: Int, y: Int) {
        if (gameStateFlow.value != GameState.Running) {
            return
        }
        val playerPlane = playerPlaneStateFlow.value
        playerPlane.x = x
        playerPlane.y = y
        if (playerPlane.animateIn) {
            playerPlane.animateIn = false
        }
        onPlayerPlaneStateFlowChange(playerPlane)
    }


    private fun onDragPlayerPlane(dragAmount: Offset) {
        if (gameStateFlow.value != GameState.Running) {
            return
        }
        val resources = getApplication<Application>().resources
        val dm = resources.displayMetrics
        val widthPixels = dm.widthPixels
        val heightPixels = dm.heightPixels
        val playerPlane = playerPlaneStateFlow.value
        val playerPlaneHeightPx = dp2px(playerPlane.height)
        var newOffsetX = playerPlane.x
        var newOffsetY = playerPlane.y

        //进行边界碰撞检测核心逻辑
        when {
            newOffsetX + dragAmount.x <= 0 -> {
                newOffsetX = 0
            }
            (newOffsetX + dragAmount.x + playerPlaneHeightPx!!) >= widthPixels -> {
                widthPixels.let {
                    newOffsetX = it - playerPlaneHeightPx
                }
            }
            else -> {
                newOffsetX += dragAmount.x.roundToInt()
            }
        }
        when {
            newOffsetY + dragAmount.y <= 0 -> {
                newOffsetY = 0
            }
            (newOffsetY + dragAmount.y) >= heightPixels -> {
                heightPixels.let {
                    newOffsetY = it
                }
            }
            else -> {
                newOffsetY += dragAmount.y.roundToInt()
            }
        }
        onPlayerPlaneMove(newOffsetX, newOffsetY)
    }


    private fun onPlayerAwardBullet(bulletAward: Int) {
        val playerPlane = playerPlaneStateFlow.value
        playerPlane.bulletAward = bulletAward
        onPlayerPlaneStateFlowChange(playerPlane)
    }


    private fun onPlayerAwardBomb(bombAward: Int) {
        val playerPlane = playerPlaneStateFlow.value
        playerPlane.bombAward = bombAward
        onPlayerPlaneStateFlowChange(playerPlane)
    }


    private fun onPlayerPlaneReBirth() {
        val playerPlane = playerPlaneStateFlow.value
        playerPlane.reBirth()
        onPlayerPlaneStateFlowChange(playerPlane)
    }


    private fun onCreateBullet() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {

                if (gameStateFlow.value == GameState.Running && playerPlaneStateFlow.value.y < getApplication<Application>().resources.displayMetrics.heightPixels) {
                    val bulletAward = playerPlaneStateFlow.value.bulletAward
                    var bulletNum = bulletAward and 0xFFFF
                    val bulletType = bulletAward shr 16
                    val bulletList = bulletListStateFlow.value

                    val firstBullet = bulletList.firstOrNull { it.isDead() }
                    if (firstBullet == null) {
                        var newBullet = Bullet(
                            type = BULLET_SINGLE,
                            drawableId = R.drawable.bullet_single,
                            width = BULLET_SPRITE_WIDTH.dp,
                            hit = 1,
                            state = SpriteState.LIFE,
                            init = false
                        )

                        if (bulletNum > 0 && bulletType == BULLET_DOUBLE) {
                            newBullet = newBullet.copy(
                                type = BULLET_DOUBLE,
                                drawableId = R.drawable.bullet_double,
                                width = 18.dp,
                                hit = 2,
                                state = SpriteState.LIFE,
                                init = false
                            )

                            bulletNum--
                            LogUtil.printLog(message = "createBullet bulletNum $bulletNum")
                            onPlayerAwardBullet(BULLET_DOUBLE shl 16 or bulletNum)
                        }
                        bulletList.add(newBullet)
                    } else {
                        var newBullet = firstBullet.copy(
                            type = BULLET_SINGLE,
                            drawableId = R.drawable.bullet_single,
                            width = BULLET_SPRITE_WIDTH.dp,
                            hit = 1,
                            state = SpriteState.LIFE,
                            init = false
                        )

                        if (bulletNum > 0 && bulletType == BULLET_DOUBLE) {
                            newBullet = firstBullet.copy(
                                type = BULLET_DOUBLE,
                                drawableId = R.drawable.bullet_double,
                                width = 18.dp,
                                hit = 2,
                                state = SpriteState.LIFE,
                                init = false
                            )
                            bulletNum--

                            onPlayerAwardBullet(BULLET_DOUBLE shl 16 or bulletNum)
                        }
                        bulletList.add(newBullet)
                        bulletList.removeAt(0)
                    }
                    onBulletListStateFlowChange(bulletList)
                }
            }
        }

    }


    private fun onBulletMove(bullet: Bullet) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {


                if (!bullet.init) {

                    val playerPlane = playerPlaneStateFlow.value
                    val playerPlaneWidthPx = dp2px(playerPlane.width)
                    val bulletWidthPx = dp2px(bullet.width)
                    val bulletHeightPx = dp2px(bullet.height)
                    val startX = (playerPlane.x + playerPlaneWidthPx!! / 2 - bulletWidthPx!! / 2)
                    val startY = (playerPlane.y - bulletHeightPx!!)
                    bullet.startX = startX
                    bullet.startY = startY
                    bullet.x = bullet.startX
                    bullet.y = bullet.startY
                    bullet.init = true
                }

                if (bullet.isInvalid()) {
                    bullet.die()
                }

                bullet.move()
            }
        }

    }


    private fun createAwardSprite() {
        LogUtil.printLog(message = "createAwardSprite() ---> ")
        if (gameStateFlow.value == GameState.Running) {
            val listAward = awardListStateFlow.value
            val type = (0..1).random()
            val award = Award(type = type)
            award.state = SpriteState.LIFE
            if (type == AWARD_BULLET) {
                award.drawableId = R.drawable.middle_enemy_plane_3
                award.amount = 100
            }
            if (type == AWARD_BOMB) {
                award.drawableId = R.drawable.small_enemy_plane3
                award.amount = 1

            }
            listAward.add(award)
            onAwardListStateFlowChange(listAward)
        }
    }

    private fun onAwardRemove(award: Award) {
        val awardList = awardListStateFlow.value
        awardList.remove(award)
        onAwardListStateFlowChange(awardList)
    }


    private fun createEnemySprite(
        smallEnemyPlaneNum: Int,
        middleEnemyPlaneNum: Int,
        bigEnemyPlaneNum: Int
    ) {

        val smallEnemyPlane = EnemyPlane(
            id = id.incrementAndGet(),
            name = "enemy",
            type = 0,
            drawableIds = listOf(R.drawable.small_enemy_plane1),
            bombDrawableId = R.drawable.bomb1,
            velocity = 10,
            segment = 3,
            power = 1,
            value = 10
        )
        val middleEnemyPlane = EnemyPlane(
            id = id.incrementAndGet(),
            name = "enemy",
            type = 1,
            drawableIds = listOf(
                R.drawable.middle_enemy_plane_1,
                R.drawable.middle_enemy_plane_2
            ),
            bombDrawableId = R.drawable.bomb3,
            width = MIDDLE_ENEMY_PLANE_SPRITE_SIZE.dp,
            height = MIDDLE_ENEMY_PLANE_SPRITE_SIZE.dp,
            velocity = 8,
            segment = 4,
            power = 4,
            value = 40
        )
        val bigEnemyPlane = EnemyPlane(
            id = id.incrementAndGet(),
            name = "enemy",
            type = 2,
            drawableIds = listOf(
                R.drawable.big_enemy_plane_1,
                R.drawable.big_enemy_plane_2,
                R.drawable.big_enemy_plane_3

            ),
            bombDrawableId = R.drawable.bomb,
            width = BIG_ENEMY_PLANE_SPRITE_SIZE.dp,
            height = BIG_ENEMY_PLANE_SPRITE_SIZE.dp,
            velocity = 2,
            segment = 6,
            power = 18,
            value = 90
        )
        val listEnemyPlane = enemyPlaneListStateFlow.value

        listEnemyPlane.add(smallEnemyPlane)
        for (small in 1 until smallEnemyPlaneNum) {
            val copy = smallEnemyPlane.copy()
            copy.id = id.incrementAndGet()
            listEnemyPlane.add(copy)
        }

        listEnemyPlane.add(middleEnemyPlane)
        for (middle in 1 until middleEnemyPlaneNum) {
            val copy = middleEnemyPlane.copy()
            copy.id = id.incrementAndGet()
            listEnemyPlane.add(copy)
        }

        for (big in 1 until bigEnemyPlaneNum) {
            val copy = bigEnemyPlane.copy()
            copy.id = id.incrementAndGet()
            listEnemyPlane.add(copy)
        }
        for (enemyPlane in listEnemyPlane) {
            LogUtil.printLog(message = "createEnemySprite: enemyPlane $enemyPlane")
        }
        onEnemyPlaneListStateFlowChange(listEnemyPlane)
    }

    private fun onEnemyPlaneMove(
        enemyPlane: EnemyPlane,
        onBombAnimChange: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val widthPixels = getApplication<Application>().resources.displayMetrics.widthPixels
                val heightPixels =
                    getApplication<Application>().resources.displayMetrics.heightPixels

                val enemyPlaneWidthPx = dp2px(enemyPlane.width)
                val enemyPlaneHeightPx = dp2px(enemyPlane.height)
                val maxEnemyPlaneSpriteX = widthPixels - enemyPlaneWidthPx!!
                val maxEnemyPlaneSpriteY = heightPixels * 1.5

                if (!enemyPlane.init) {
                    enemyPlane.x = (0..maxEnemyPlaneSpriteX).random()
                    var newY = -(0..heightPixels).random() - (0..heightPixels).random()
                    when (enemyPlane.type) {
                        0 -> newY -= enemyPlaneHeightPx!! * 2
                        1 -> newY -= enemyPlaneHeightPx!! * 4
                        2 -> newY -= enemyPlaneHeightPx!! * 10
                    }
                    enemyPlane.y = newY
                    enemyPlane.init = true
                    enemyPlane.reBirth()
                }

                if (enemyPlane.y >= maxEnemyPlaneSpriteY) {
                    enemyPlane.init = false
                    enemyPlane.die()
                }
                enemyPlane.move()

                onCollisionDetect(enemyPlane, onBombAnimChange)
            }
        }


    }

    private fun onCollisionDetect(
        enemyPlane: EnemyPlane,
        onBombAnimChange: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                if (enemyPlane.isAlive() && enemyPlane.isNoPower()) {
                    enemyPlane.die()
                    onBombAnimChange(true)
                }

                val enemyPlaneWidthPx = dp2px(enemyPlane.width)
                val enemyPlaneHeightPx = dp2px(enemyPlane.height)

                val playerPlane = playerPlaneStateFlow.value
                val playerPlaneWidthPx = dp2px(playerPlane.width)
                val playerPlaneHeightPx = dp2px(playerPlane.height)

                if (enemyPlane.isAlive() && playerPlane.x > 0 && playerPlane.y > 0 && enemyPlane.x > 0 && enemyPlane.y > 0 && SpriteUtil.isCollisionWithRect(
                        playerPlane.x,
                        playerPlane.y,
                        playerPlaneWidthPx!!,
                        playerPlaneHeightPx!!,
                        enemyPlane.x,
                        enemyPlane.y,
                        enemyPlaneWidthPx!!,
                        enemyPlaneHeightPx!!
                    )
                ) {
                    if (gameStateFlow.value == GameState.Running) {
                        if (playerPlane.isNoProtect()) {
                            onGameAction.die()
                        }
                    }

                }

                val bulletList = bulletListStateFlow.value
                if (bulletList.isEmpty()) {
                    return@withContext
                }
                val firstBullet = bulletList.first()
                val bulletSpriteWidthPx = dp2px(firstBullet.width)
                val bulletSpriteHeightPx = dp2px(firstBullet.height)

                bulletList.forEach { bullet ->
                    if (enemyPlane.isAlive() && bullet.isAlive() && bullet.x > 0 && bullet.y > 0 && SpriteUtil.isCollisionWithRect(
                            bullet.x,
                            bullet.y,
                            bulletSpriteWidthPx!!,
                            bulletSpriteHeightPx!!,
                            enemyPlane.x,
                            enemyPlane.y,
                            enemyPlaneWidthPx!!,
                            enemyPlaneHeightPx!!
                        )
                    ) {
                        bullet.die()
                        enemyPlane.beHit(bullet.hit)
                        if (enemyPlane.isNoPower()) {
                            enemyPlane.die()
                            onBombAnimChange(true)
                            onGameScore(gameScoreStateFlow.value + enemyPlane.value)
                            return@forEach
                        }
                    }
                }
            }

        }

    }



    private fun onDestroyAllEnemy() {
        viewModelScope.launch {
            val listEnemyPlane = enemyPlaneListStateFlow.value
            var countScore = 0
            withContext(Dispatchers.Default) {
                for (enemyPlane in listEnemyPlane) {
                    if (enemyPlane.isAlive() && !enemyPlane.isNoPower() && enemyPlane.y > 0 && enemyPlane.y < getApplication<Application>().resources.displayMetrics.heightPixels) {
                        countScore += enemyPlane.value
                        enemyPlane.bomb()
                    }
                }
                _enemyPlaneListStateFlow.emit(listEnemyPlane)
            }
            gameScoreStateFlow.value.plus(countScore).let { onGameScoreStateFlowChange(it) }

            val bombAward = playerPlaneStateFlow.value.bombAward
            var bombNum = bombAward and 0xFFFF
            val bombType = bombAward shr 16
            if (bombNum-- <= 0) {
                bombNum = 0
            }
            onPlayerAwardBomb(bombType shl 16 or bombNum)
        }
    }

    private fun onAwardMove(award: Award) {
        val widthPixels = getApplication<Application>().resources.displayMetrics.widthPixels
        val heightPixels = getApplication<Application>().resources.displayMetrics.heightPixels

        val awardWidthPx = dp2px(award.width)
        val awardHeightPx = dp2px(award.height)
        val maxAwardSpriteX = widthPixels - awardWidthPx!!
        val maxAwardSpriteY = heightPixels * 1.5

        if (!award.init) {
            award.startX = (0..maxAwardSpriteX).random()
            award.startY = -awardHeightPx!!.toInt()
            award.x = award.startX
            award.y = award.startY
            award.init = true
        }

        if (award.isAlive() && award.y >= maxAwardSpriteY) {
            award.die()
        }

        award.move()

        val playerPlane = playerPlaneStateFlow.value
        val playerPlaneWidthPx = dp2px(playerPlane.width)
        val playerPlaneHeightPx = dp2px(playerPlane.height)

        if (playerPlane.isAlive() && playerPlane.x > 0 && playerPlane.y > 0 && award.isAlive() && award.x > 0 && award.y > 0 && SpriteUtil.isCollisionWithRect(
                playerPlane.x,
                playerPlane.y,
                playerPlaneWidthPx!!,
                playerPlaneHeightPx!!,
                award.x,
                award.y,
                awardWidthPx,
                awardHeightPx!!,
            )
        ) {
            onGetAward(award)
            award.die()
        }
    }


    private fun onGameScore(score: Int) {
        onGameScoreStateFlowChange(score)
        onGameLevelUp()

        if (score % 100 == 0) {
            createAwardSprite()
        }
    }

    private fun onGetAward(award: Award) {
        if (award.type == AWARD_BULLET) {
            val bulletAward = playerPlaneStateFlow.value.bulletAward
            var num = bulletAward and 0xFFFF
            num += award.amount
            onPlayerAwardBullet(BULLET_DOUBLE shl 16 or num)
        }
        if (award.type == AWARD_BOMB) {
            val bombAward = playerPlaneStateFlow.value.bombAward
            var num = bombAward and 0xFFFF
            num += award.amount
            onPlayerAwardBomb(0 shl 16 or num)
        }

        onAwardRemove(award)
    }

    private fun onGameLevelUp() {
        val score = gameScoreStateFlow.value
        if (score in 100..999) {
            onGameLevelStateFlowChange(2)
        }
        if (score in 1000..1999) {
            onGameLevelStateFlowChange(3)
        }
    }

    private fun onGameReset() {

        onGameScoreStateFlowChange(0)

        onPlayerPlaneReBirth()

    }



    val onGameAction = GameAction(
        start = {
            onGameStateFlowChange(GameState.Running)
        },
        reset = {
            onGameReset()
            onGameStateFlowChange(GameState.Waiting)
        },
        pause = {
            onGameStateFlowChange(GameState.Paused)

        },
        movePlayerPlane = { x, y ->
            onPlayerPlaneMove(x, y)
        },
        dragPlayerPlane = { dragAmount ->
            onDragPlayerPlane(dragAmount)
        },
        die = {
            onGameStateFlowChange(GameState.Dying)
        },
        over = {
            onGameStateFlowChange(GameState.Over)
        },
        exit = {
            onGameStateFlowChange(GameState.Exit)
        },
        destroyAllEnemy = {
            onDestroyAllEnemy()
        },
        moveEnemyPlane = { enemyPlane, onBombAnimChange ->
            onEnemyPlaneMove(enemyPlane, onBombAnimChange)
        },
        moveAward = {
            onAwardMove(it)
        },
        createBullet = {
            onCreateBullet()
        },
        moveBullet = {
            onBulletMove(it)
        },
    )
}

@InternalCoroutinesApi
fun GameViewModel.dp2px(dp: Dp): Int? {
    val resources = getApplication<Application>().resources
    return DensityUtil.dp2px(resources, dp.value.roundToInt())
}