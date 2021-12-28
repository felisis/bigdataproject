package com.felis.mldl.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.felis.mldl.databinding.Maintip1ActivityBinding

class Maintip1Activity : AppCompatActivity() {

    private lateinit var binding: Maintip1ActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Maintip1ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inflate the layout for this fragment

        binding.nextbtn.setOnClickListener {
            val intent = Intent(this, Maintip2Activity::class.java)
            startActivity(intent)
        }
    }


}