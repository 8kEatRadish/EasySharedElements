package com.sniperking.eatradish.utils

import android.view.View

fun View.show() = apply {
    alpha = 0f
    translationY = 10f
    pivotX = 0f
    pivotY = 0f
    animate().setDuration(300).alpha(1f).translationY(0f).start()
}

fun View.disappear() = apply {
    alpha = 1f
    translationY = 0f
    pivotX = 0f
    pivotY = 0f
    animate().setDuration(300).alpha(0f).translationY(-10f).start()
}