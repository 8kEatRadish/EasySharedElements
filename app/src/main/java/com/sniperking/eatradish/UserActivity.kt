package com.sniperking.eatradish

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sniperking.eatradish.annotations.*
import com.sniperking.eatradish.uti.disappear
import com.sniperking.eatradish.uti.show
import com.sniperking.runtime.TimeInterpolatorType

@Builder
class UserActivity : AppCompatActivity() {

    @Required
    lateinit var name: String

    @Required
    lateinit var age: String

    @Optional(stringValue = "")
    lateinit var url: String

    @Optional(stringValue = "")
    lateinit var group: String

    @Optional(stringValue = "黄色")
    lateinit var color: String

    @SuppressLint("NonConstantResourceId")
    @SharedElement(
        name = "userImage",
        resId = R.id.user_image,
        runEnterAnimDuration = 500,
        runEnterTimeInterpolatorType = TimeInterpolatorType.OVERSHOOT_INTERPOLATOR,
        runExitTimeInterpolatorType = TimeInterpolatorType.LINEAR_INTERPOLATOR
    )
    lateinit var imageView: ImageView

    @SharedElement(
        name = "userImage2",
        resId = R.id.user_image2,
        runEnterAnimDuration = 1000,
        runEnterTimeInterpolatorType = TimeInterpolatorType.CYCLE_INTERPOLATOR,
        runExitTimeInterpolatorType = TimeInterpolatorType.PATH_INTERPOLATOR
    )
    lateinit var imageView2: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        UserActivityBuilder.inject(this, savedInstanceState)
        Toast.makeText(
            application,
            "name : $name ; age : $age ; url : $url ; group : $group ; color : $color",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onBackPressed() {
        findViewById<TextView>(R.id.user_text).disappear()
        UserActivityBuilder.runExitAnim(this, 300)
    }

    @RunEnterAnim(callBackState = RunEnterAnim.RunEnterAnimState.START)
    fun startEnterAnim() {
        findViewById<TextView>(R.id.user_text).alpha = 0f
    }

    @RunEnterAnim(callBackState = RunEnterAnim.RunEnterAnimState.END)
    fun endEnterAnim() {
        findViewById<TextView>(R.id.user_text).show()
    }
}