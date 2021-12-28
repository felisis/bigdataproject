package com.felis.mldl.camera

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.felis.mldl.R
import com.felis.mldl.databinding.LoadingActivityBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class LoadingActivity : AppCompatActivity() {
    private lateinit var binding: LoadingActivityBinding
    lateinit var firestore:FirebaseFirestore
    lateinit var storage:FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoadingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        Glide.with(this).load(R.drawable.morph1).into(binding.loadingimg)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(baseContext, MorphingendActivity::class.java)
            startActivity(intent)
            finish()
        }, 10000)

    }
}
