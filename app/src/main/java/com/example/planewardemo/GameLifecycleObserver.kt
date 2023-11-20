package com.example.planewardemo

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.planewardemo.util.LogUtil
import com.example.planewardemo.viewmodel.GameViewModel
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi
class GameLifecycleObserver(gameViewModel: GameViewModel) : DefaultLifecycleObserver {

    companion object {
        const val TAG = "GameLifecycleObserver"
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        LogUtil.printLog(TAG, "onCreate()")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        LogUtil.printLog(TAG, "onResume()")
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        LogUtil.printLog(TAG, "onDestroy()")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        LogUtil.printLog(TAG, "onDestroy()")
    }

}
