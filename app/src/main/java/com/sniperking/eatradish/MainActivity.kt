package com.sniperking.eatradish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sniperking.eatradish.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.goToDetailsActivity.setOnClickListener {
            DetailsActivityBuilder.start(this, "小王很美丽", "小王", 1994, "www.google.com")
        }
        binding.goToUserActivity.setOnClickListener {
            UserActivityBuilder.start(this, "7岁", "小王", "黄色", "神族", "www.baidu.com")
        }
    }
}