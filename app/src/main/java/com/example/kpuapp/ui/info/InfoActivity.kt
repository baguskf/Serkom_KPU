package com.example.kpuapp.ui.info

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.kpuapp.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {
    private lateinit var binding : ActivityInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                Toast.makeText(this@InfoActivity, "Web berhasil dimuat", Toast.LENGTH_LONG).show()
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.webView.loadUrl("https://www.kpu.go.id/")
        binding.webView.settings.javaScriptEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

}