package com.felis.mldl.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.felis.mldl.R
import com.felis.mldl.activity.WelcomeActivity
import com.felis.mldl.databinding.MainActivityBinding
import com.felis.mldl.camera.CameraFragment
import com.google.gson.GsonBuilder
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager


class MainActivity : AppCompatActivity() {

    var imm: InputMethodManager? = null

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        // 터치시 키보드 내림

        initNavigationBar()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://15.164.24.10:8000/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

        val logoutService = retrofit.create(LogoutService::class.java)
        val intent = Intent(this, WelcomeActivity::class.java)

        binding.logoutbtn.setOnClickListener()
        {
            logoutService.getLogout().enqueue(object : Callback<Logout> {
                override fun onResponse(call: Call<Logout>, response: Response<Logout>) {
                    Toast.makeText(this@MainActivity, "로그아웃 성공", Toast.LENGTH_LONG).show()
                    startActivity(intent)
                }

                override fun onFailure(call: Call<Logout>, t: Throwable) {
                }
            })
        }

    }


    fun initNavigationBar() {
        binding.bottomNavigationView.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_name -> {
                        changeFragment(HomeFragment())
                    }
                    R.id.menu_cam -> {
                        changeFragment(CameraFragment())
                    }
                }
                true
            }
            selectedItemId = R.id.menu_name
        }
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.frameLayout.id, fragment).commit()
    }

    fun hidekeyboard(v: View) {
        if (v != null) {
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

}