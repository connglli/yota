package io.github.clixyz.yota.events


import io.github.clixyz.yota.droid.Droid

open class YotaLongTapEvent(val x: Float, val y: Float) : YotaEvent {

    companion object {
        fun mustFailEvent() = YotaLongTapEvent(-1f, -1f)
    }

    override fun inject(): Int = if (x < 0 || y < 0) {
        YotaEvent.INJECT_FAILED
    } else if (Droid.exec{ it.im.longTap(x, y) }) {
        YotaEvent.INJECT_SUCCEEDED
    } else {
        YotaEvent.INJECT_FAILED
    }
}