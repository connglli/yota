package io.github.clixyz.yota.events

import io.github.clixyz.yota.droid.Droid

open class YotaTapEvent(val x: Int, val y: Int) : YotaEvent {

    companion object {
        fun mustFailEvent() = YotaTapEvent(-1, -1)
    }

    override fun inject(): Int {
        return if (x < 0 || y < 0) {
            YotaEvent.INJECT_FAILED
        } else if (Droid.exec{ it.im.tap(x, y) }) {
            YotaEvent.INJECT_SUCCEEDED
        } else {
            YotaEvent.INJECT_FAILED
        }
    }
}