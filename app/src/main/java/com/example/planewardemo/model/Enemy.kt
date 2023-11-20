package com.example.planewardemo.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.planewardemo.R
import kotlinx.coroutines.InternalCoroutinesApi


enum class SpriteState {
    LIFE,
    DEATH,
}


@InternalCoroutinesApi
open class Sprite(
    open var id: Long = System.currentTimeMillis(),
    open var name: String = "plane",
    open var type: Int = 0,
    @DrawableRes open val drawableIds: List<Int> = listOf(
        R.drawable.player_plane_1,
        R.drawable.player_plane_2
    ),
    @DrawableRes open val bombDrawableId: Int = R.drawable.bomb2,
    open var segment: Int = 14,
    open var x: Int = 0,
    open var y: Int = 0,
    open var startX: Int = -100,
    open var startY: Int = -100,
    open var width: Dp = BULLET_SPRITE_WIDTH.dp,
    open var height: Dp = BULLET_SPRITE_HEIGHT.dp,
    open var velocity: Int = 40,
    open var state: SpriteState = SpriteState.LIFE,
    open var init: Boolean = false,
) {

    fun isAlive() = state == SpriteState.LIFE

    fun isDead() = state == SpriteState.DEATH

    open fun reBirth() {
        state = SpriteState.LIFE
    }

    open fun die() {
        state = SpriteState.DEATH
    }

    override fun toString(): String {
        return "Sprite(id=$id, name='$name', drawableIds=$drawableIds, bombDrawableId=$bombDrawableId, segment=$segment, x=$x, y=$y, width=$width, height=$height, state=$state)"
    }
}



const val PLAYER_PLANE_SPRITE_SIZE = 60
const val PLAYER_PLANE_PROTECT = 60

@InternalCoroutinesApi
data class PlayerPlane(
    override var id: Long = System.currentTimeMillis(),
    override var name: String = "雷电",
    @DrawableRes override val drawableIds: List<Int> = listOf(
        R.drawable.player_plane_1,
        R.drawable.player_plane_2
    ),
    @DrawableRes val bombDrawableIds: Int = R.drawable.bomb4,
    override var segment: Int = 4,
    override var x: Int = -100,
    override var y: Int = -100,
    override var width: Dp = PLAYER_PLANE_SPRITE_SIZE.dp,
    override var height: Dp = PLAYER_PLANE_SPRITE_SIZE.dp,
    var protect: Int = PLAYER_PLANE_PROTECT,
    var life: Int = 1,
    var animateIn: Boolean = true,
    var bulletAward: Int = BULLET_DOUBLE shl 16 or 0,
    var bombAward: Int = 0 shl 16 or 0,
) : Sprite() {

    fun reduceProtect() {
        if (protect > 0) {
            protect--
        }
    }

    fun isNoProtect() = protect <= 0

    override fun reBirth() {
        state = SpriteState.LIFE
        animateIn = true
        x = startX
        y = startY
        protect = PLAYER_PLANE_PROTECT
        bulletAward = 0
        bombAward = 0
    }
}

const val BULLET_SPRITE_WIDTH = 6
const val BULLET_SPRITE_HEIGHT = 18
const val BULLET_SINGLE = 0
const val BULLET_DOUBLE = 1

@InternalCoroutinesApi
data class Bullet(
    override var id: Long = System.currentTimeMillis(),
    override var name: String = "蓝色单发子弹",
    override var type: Int = BULLET_SINGLE,
    @DrawableRes val drawableId: Int = R.drawable.bullet_single,
    override var width: Dp = BULLET_SPRITE_WIDTH.dp,
    override var height: Dp = BULLET_SPRITE_HEIGHT.dp,
    override var x: Int = 0,
    override var y: Int = 0,
    override var state: SpriteState = SpriteState.DEATH,
    override var init: Boolean = false,
    var hit: Int = 1,
) : Sprite() {


    fun isInvalid() = this.y < 0


    fun move() {
        this.x = this.startX
        this.y -= this.velocity
    }
}


const val AWARD_BULLET = 0
const val AWARD_BOMB = 1

@InternalCoroutinesApi
data class Award(
    override var id: Long = System.currentTimeMillis(),
    override var name: String = "enemy",
    override var type: Int = AWARD_BULLET,
    @DrawableRes var drawableId: Int = R.drawable.middle_enemy_plane_3,
    override var width: Dp = 50.dp,
    override var height: Dp = 80.dp,
    override var velocity: Int = 20,
    override var x: Int = 0,
    override var y: Int = 0,
    override var state: SpriteState = SpriteState.DEATH,
    override var init: Boolean = false,
    var amount: Int = 1,

) : Sprite(){

    fun move() {
        this.y += this.velocity
    }
}

const val SMALL_ENEMY_PLANE_SPRITE_SIZE = 40
const val MIDDLE_ENEMY_PLANE_SPRITE_SIZE = 60
const val BIG_ENEMY_PLANE_SPRITE_SIZE = 100

@InternalCoroutinesApi
data class EnemyPlane(
    override var id: Long = System.currentTimeMillis(),
    override var name: String = "enemy",
    override var type: Int = 0,
    @DrawableRes override val drawableIds: List<Int> = listOf(R.drawable.small_enemy_plane1),
    @DrawableRes override val bombDrawableId: Int = R.drawable.bomb1,
    override var segment: Int = 3,
    override var x: Int = 0,
    override var y: Int = 0,
    override var width: Dp = SMALL_ENEMY_PLANE_SPRITE_SIZE.dp,
    override var height: Dp = SMALL_ENEMY_PLANE_SPRITE_SIZE.dp,
    override var velocity: Int = 1,
    var bombX: Int = -100,
    var bombY: Int = -100,
    val power: Int = 1,
    var hit: Int = 0,
    val value: Int = 10,

) : Sprite() {

    fun beHit(reduce: Int) {
        hit += reduce
    }

    fun isNoPower() = (power - hit) <= 0

    fun bomb() {
        hit = power
    }

    fun move() {
        this.y += this.velocity
    }

    fun getRealDrawableId(): Int {
        var realDrawableId = drawableIds[0]
        if (hit > 0) {
            val hitPerPower = hit / (power / drawableIds.size)
            val drawableIdsIndex = when {
                hitPerPower < 0 -> {
                    0
                }
                hitPerPower >= drawableIds.size -> {
                    drawableIds.size - 1
                }
                else -> {
                    hitPerPower
                }
            }
            realDrawableId = drawableIds[drawableIdsIndex]
        }
        return realDrawableId
    }

    override fun reBirth() {
        state = SpriteState.LIFE
        hit = 0
    }

    override fun die() {
        state = SpriteState.DEATH
        bombX = x
        bombY = y
    }

    override fun toString(): String {
        return "EnemyPlane(state=$state, id=$id, name='$name', drawableIds=$drawableIds, bombDrawableId=$bombDrawableId, segment=$segment, x=$x, y=$y, width=$width, height=$height, power=$power, hit=$hit, value=$value, startY=$startY)"
    }


}

@InternalCoroutinesApi
data class Bomb(
    override var id: Long = System.currentTimeMillis(),
    override var name: String = "bomb",
    @DrawableRes override var bombDrawableId: Int = R.drawable.bomb2,
    override var segment: Int = 14,
    override var x: Int = 200,
    override var y: Int = 200,
    override var width: Dp = BIG_ENEMY_PLANE_SPRITE_SIZE.dp,
    override var height: Dp = BIG_ENEMY_PLANE_SPRITE_SIZE.dp,
    override var state: SpriteState = SpriteState.DEATH
) : Sprite()