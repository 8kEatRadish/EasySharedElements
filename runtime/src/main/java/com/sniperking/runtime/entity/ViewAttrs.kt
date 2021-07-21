package com.sniperking.runtime.entity

import android.animation.TimeInterpolator
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.time.Duration

/**
 *文件: ViewAttrs.kt
 *描述: 跳转的时候view的属性
 *作者: SuiHongWei 2020/6/9
 **/
class ViewAttrs : Parcelable {
    var id: Int
    var alpha: Float
    var screenX: Int
    var screenY: Int
    var width: Int
    var height: Int
    var runEnterAnimDuration: Long
    var runExitAnimDuration: Long
    var runEnterAnimTimeInterpolatorType: Int
    var runExitAnimTimeInterpolatorType: Int

    constructor(
        id: Int,
        alpha: Float,
        screenX: Int,
        screenY: Int,
        width: Int,
        height: Int,
        runEnterAnimDuration: Long = 800,
        runExitAnimDuration: Long = 800,
        runEnterAnimTimeInterpolatorType: Int = 0,
        runExitAnimTimeInterpolatorType: Int = 0
    ) {
        this.id = id
        this.alpha = alpha
        this.screenX = screenX
        this.screenY = screenY
        this.width = width
        this.height = height
        this.runEnterAnimDuration = runEnterAnimDuration
        this.runExitAnimDuration = runExitAnimDuration
        this.runEnterAnimTimeInterpolatorType = runEnterAnimTimeInterpolatorType
        this.runExitAnimTimeInterpolatorType = runExitAnimTimeInterpolatorType
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeFloat(alpha)
        dest.writeInt(screenX)
        dest.writeInt(screenY)
        dest.writeInt(width)
        dest.writeInt(height)
        dest.writeLong(runEnterAnimDuration)
        dest.writeLong(runExitAnimDuration)
        dest.writeInt(runEnterAnimTimeInterpolatorType)
        dest.writeInt(runExitAnimTimeInterpolatorType)
    }

    private constructor(parcel: Parcel) {
        id = parcel.readInt()
        alpha = parcel.readFloat()
        screenX = parcel.readInt()
        screenY = parcel.readInt()
        width = parcel.readInt()
        height = parcel.readInt()
        runEnterAnimDuration = parcel.readLong()
        runExitAnimDuration = parcel.readLong()
        runEnterAnimTimeInterpolatorType = parcel.readInt()
        runExitAnimTimeInterpolatorType = parcel.readInt()
    }

    companion object CREATOR : Parcelable.Creator<ViewAttrs> {
        override fun createFromParcel(source: Parcel): ViewAttrs {
            return ViewAttrs(source)
        }

        override fun newArray(size: Int): Array<ViewAttrs?> {
            return arrayOfNulls(size)
        }
    }
}