package com.sniperking.eatradish

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sniperking.eatradish.annotations.Builder
import com.sniperking.eatradish.annotations.Optional
import com.sniperking.eatradish.annotations.Required

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        Toast.makeText(
            this,
            "name : $name ; details : $details ; url : $url ; createAt : $createAt",
            Toast.LENGTH_SHORT
        ).show()
    }
}