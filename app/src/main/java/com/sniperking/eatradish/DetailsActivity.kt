package com.sniperking.eatradish

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sniperking.eatradish.annotations.*
import com.sniperking.eatradish.uti.disappear
import com.sniperking.eatradish.uti.show

@Builder
class DetailsActivity : AppCompatActivity() {

    @Required
    lateinit var name: String

    @Required
    lateinit var details: String

    @Optional(stringValue = "")
    lateinit var url: String

    @Optional
    var createAt: Long = 0L

    @SharedElement(name = "img", resId = R.id.details_image)
    lateinit var testImage: ImageView

    @SharedElement(name = "img2", resId = R.id.details_image2)
    lateinit var testImage2: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        DetailsActivityBuilder.inject(this, savedInstanceState)
        Toast.makeText(
            application,
            "name : $name ; details : $details ; url : $url ; createAt : $createAt",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onBackPressed() {
        findViewById<TextView>(R.id.details_text).disappear()
        DetailsActivityBuilder.runExitAnim(this, 300)
    }

    @RunEnterAnim(callBackState = RunEnterAnim.RunEnterAnimState.START)
    fun startEnterAnim() {
        findViewById<TextView>(R.id.details_text).alpha = 0f
    }

    @RunEnterAnim(callBackState = RunEnterAnim.RunEnterAnimState.END)
    fun endEnterAnim() {
        findViewById<TextView>(R.id.details_text).show()
    }
}