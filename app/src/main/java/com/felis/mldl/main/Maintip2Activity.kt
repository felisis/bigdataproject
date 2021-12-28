package com.felis.mldl.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.felis.mldl.databinding.Maintip2ActivityBinding

class Maintip2Activity: AppCompatActivity() {

    private lateinit var binding : Maintip2ActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Maintip2ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inflate the layout for this fragment


        binding.nextbtn.setOnClickListener {
            val intent = Intent(this, Maintip3Activity::class.java)
            startActivity(intent)
        }
    }


}