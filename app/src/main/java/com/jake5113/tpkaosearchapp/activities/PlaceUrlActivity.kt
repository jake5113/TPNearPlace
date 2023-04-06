package com.jake5113.tpkaosearchapp.activities

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.jake5113.tpkaosearchapp.databinding.ActivityPlaceUrlBinding

class PlaceUrlActivity : AppCompatActivity() {

    private val binding: ActivityPlaceUrlBinding by lazy {
        ActivityPlaceUrlBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.wv.webViewClient = WebViewClient() // 현재 웹뷰 안에서 웹문서 열리도록
        binding.wv.webChromeClient = WebChromeClient() // 웹문서 안에서 다이얼로그 같은 것을 발동하도록

        binding.wv.settings.javaScriptEnabled = true // 웹뷰는 기본적으로 보안문제로 JS 동작을 막아놓았기에 이를 허용

        var place_url: String = intent.getStringExtra("place_url") ?: ""
        binding.wv.loadUrl(place_url)
    }


    override fun onBackPressed() {
        //super.onBackPressed()
        if (binding.wv.canGoBack()) binding.wv.goBack()
        else super.onBackPressed()
    }
}