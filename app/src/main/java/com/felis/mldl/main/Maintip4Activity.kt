package com.felis.mldl.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.felis.mldl.databinding.Maintip4ActivityBinding

class Maintip4Activity: AppCompatActivity() {

    private lateinit var binding : Maintip4ActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Maintip4ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inflate the layout for this fragment



        binding.nextbtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


}