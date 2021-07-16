package com.sniperking.eatradish

import android.app.Application
import com.sniperking.runtime.ActivityBuilder

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ActivityBuilder.INSTANCE.init(this)
    }
}