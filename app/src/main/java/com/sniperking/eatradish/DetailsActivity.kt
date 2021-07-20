package com.sniperking.eatradish

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
    lateinit var testImage2:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        DetailsActivityBuilder.inject(this,savedInstanceState)
        Toast.makeText(
                application,
                "name : $name ; details : $details ; url : $url ; createAt : $createAt",
                Toast.LENGTH_SHORT
        ).show()
    }

    override fun onBackPressed() {
        DetailsActivityBuilder.runExitAnim(this,300)
    }
}