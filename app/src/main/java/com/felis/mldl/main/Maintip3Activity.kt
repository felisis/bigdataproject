package com.felis.mldl.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.felis.mldl.databinding.Maintip3ActivityBinding

class Maintip3Activity: AppCompatActivity() {

    private lateinit var binding : Maintip3ActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Maintip3ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inflate the layout for this fragment



        binding.nextbtn.setOnClickListener {
            val intent = Intent(this, Maintip4Activity::class.java)
            startActivity(intent)
        }
    }


}