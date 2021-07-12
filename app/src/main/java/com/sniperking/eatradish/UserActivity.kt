package com.sniperking.eatradish

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sniperking.eatradish.annotations.Builder
import com.sniperking.eatradish.annotations.Optional
import com.sniperking.eatradish.annotations.Required

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        Toast.makeText(
            this,
            "name : $name ; age : $age ; url : $url ; group : $group ; color : $color",
            Toast.LENGTH_SHORT
        ).show()
    }
}