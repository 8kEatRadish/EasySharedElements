package com.sniperking.eatradish

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sniperking.eatradish.annotations.Builder
import com.sniperking.eatradish.annotations.Optional
import com.sniperking.eatradish.annotations.Required
import com.sniperking.eatradish.annotations.SharedElement
import com.sniperking.runtime.utils.AnimationUtils

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
    @SharedElement(name = "userImage", resId = R.id.user_image)
    lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        UserActivityBuilder.inject(this,savedInstanceState)
        Toast.makeText(
                application,
                "name : $name ; age : $age ; url : $url ; group : $group ; color : $color",
                Toast.LENGTH_SHORT
        ).show()
    }
    override fun onBackPressed() {
        AnimationUtils.runExitAnim(this,intent.getParcelableExtra("imageView"),300)
    }
}