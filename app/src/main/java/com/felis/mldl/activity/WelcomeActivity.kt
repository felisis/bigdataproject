package com.felis.mldl.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.felis.mldl.R
import com.felis.mldl.databinding.WelcomeActivityBinding
import com.felis.mldl.login.LoginActivity
import com.felis.mldl.register.RegisterActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: WelcomeActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)
        binding = WelcomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var intent1 = Intent(this, LoginActivity::class.java)
        var intent2 = Intent(this, RegisterActivity::class.java)


        binding.SigninBtn.setOnClickListener {
            startActivity(intent1)
        }

        binding.SignupBtn.setOnClickListener {
            startActivity(intent2)
        }

    }
}