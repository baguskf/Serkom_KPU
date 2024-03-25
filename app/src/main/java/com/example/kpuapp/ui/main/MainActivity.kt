package com.example.kpuapp.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.kpuapp.ui.info.InfoActivity
import com.example.kpuapp.ui.input.InputDataActivity
import com.example.kpuapp.ui.show.TampilDataActivity
import com.example.kpuapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        playanimate()

        binding.btnInformasi.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

        binding.btnForm.setOnClickListener {
            val intent = Intent(this, InputDataActivity::class.java)
            startActivity(intent)
        }

        binding.btnData.setOnClickListener {
            val intent = Intent(this, TampilDataActivity::class.java)
            startActivity(intent)
        }

        binding.btnKeluar.setOnClickListener {
            finishAffinity()
        }
    }

    private fun playanimate() {
        val photo = ObjectAnimator.ofFloat(binding.imageViewLogo, View.ALPHA, 1f).setDuration(1000)
        val info = ObjectAnimator.ofFloat(binding.btnInformasi, View.ALPHA, 1f).setDuration(500)
        val form = ObjectAnimator.ofFloat(binding.btnForm, View.ALPHA, 1f).setDuration(500)
        val data = ObjectAnimator.ofFloat(binding.btnData, View.ALPHA, 1f).setDuration(500)
        val out = ObjectAnimator.ofFloat(binding.btnKeluar, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                photo,
                info,
                form,
                data,
                out,
            )
            start()
        }
    }

}