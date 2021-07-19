package com.sniperking.eatradish

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sniperking.eatradish.annotations.Builder
import com.sniperking.eatradish.annotations.Optional
import com.sniperking.eatradish.annotations.Required
import com.sniperking.eatradish.annotations.SharedElement

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

    @SharedElement(name = "imageView", resId = R.id.details_image)
    lateinit var imageView: ImageView


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
}