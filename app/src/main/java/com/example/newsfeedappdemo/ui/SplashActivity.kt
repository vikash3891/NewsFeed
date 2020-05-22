package com.example.newsfeedappdemo.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.newsfeedappdemo.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh)

        Handler().postDelayed(Runnable {


            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2500)
    }


}
