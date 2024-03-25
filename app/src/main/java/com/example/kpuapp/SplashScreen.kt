package com.example.kpuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.kpuapp.ui.main.MainActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        },3000)
    }
}