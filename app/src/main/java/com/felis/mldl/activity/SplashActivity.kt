package com.felis.mldl.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.felis.mldl.R
import com.felis.mldl.camera.MorphingendActivity
import com.felis.mldl.databinding.SplashActivityBinding
import com.felis.mldl.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: SplashActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this).load(R.drawable.splash_gif).into(binding.splash1)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(baseContext, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}
