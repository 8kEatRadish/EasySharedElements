package com.sniperking.runtime.entity

import android.os.Parcel
import android.os.Parcelable

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
    var priority: Int

    constructor(id: Int, alpha: Float, screenX: Int, screenY: Int, width: Int, height: Int, priority: Int) {
        this.id = id
        this.alpha = alpha
        this.screenX = screenX
        this.screenY = screenY
        this.width = width
        this.height = height
        this.priority = priority
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
        dest.writeInt(priority)
    }

    private constructor(parcel: Parcel) {
        id = parcel.readInt()
        alpha = parcel.readFloat()
        screenX = parcel.readInt()
        screenY = parcel.readInt()
        width = parcel.readInt()
        height = parcel.readInt()
        priority = parcel.readInt()
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